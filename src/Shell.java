import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.Scanner;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.nio.file.Files;
import java.util.ArrayList;
import java.lang.ProcessBuilder;
import java.util.Arrays;
import java.io.File;
import java.util.List;
import java.lang.Process;


public class Shell{
	float time = 0.0f; //used for child process time
	String Directory = ""; //used for keeping track of directory
	ArrayList<String> History = new ArrayList<String>(); //used for keeping track of history
	public Shell(String[] args) throws IOException{
		Directory = System.getProperty("user.dir"); //get the starting user directory. all directory changes will be here
		Scanner inputStream = new Scanner(System.in); //used for reading in lines
		String inputString = "";
		while(true){ //the main shell loop
			System.out.format("[%s]: ", Directory); //display the current dir
			try{
				inputString = inputStream.nextLine(); //read a line of input
				History.add(inputString); // adds the command to history
			} catch (Exception e){
				System.exit(0);
			}
			String[] parsed = parse(inputString); //parses string to a string array to remove the spaces
			execute(parsed); //run the parsed command
		}
	}

	public void execute(String[] parsed){
		if(parsed.length > 0){ //used to make sure the user did not input an empty command. without this an empty command throws exception
			switch(parsed[0]){
				case "ptime": //displays time spent on child processes
					System.out.format("Total time in child processes: %.4f\n", time);
					break;
				case "here": //displays current directory
					Builtins.here(Directory);
					break;
				case "exit": //exits the shell
					System.exit(0);
					break;
				case "list": //lists all files and directories in CWD
					Builtins.list(Directory);
					break;
				case "cd": //changes directory
					if(parsed.length > 1) //change to new directory
						Directory = Builtins.cd(Directory, parsed[1]);
					else if(parsed.length == 1) //change to user home
						Directory = System.getProperty("user.home");
					break;
				case "mdir": //make a directory
					if(parsed.length > 1) //make sure arguments are correct
						Builtins.mdir(Directory, parsed[1]);
					break;
				case "rdir": //remove a directory(recursively)
					if(parsed.length > 1)
						Builtins.rdir(Directory + "/" + parsed[1]);
					break;
				case "history": //display history
					Builtins.history(History);
					break;
				case "^": //run past command
					if(parsed.length > 1){
						try{
							int n = Integer.parseInt(parsed[1]);
							if(n < History.size()){
								String[] b = parse(History.get(n - 1));
								execute(b);
							}
							else{
								System.out.format("%d is to large for the current history\n", n);
							}
						}
						catch(Exception e){
							System.out.format("The command ^ requires an int to follow and %s is not an int.\n", parsed[1]);
						}
					}
					break;
				default: //used if no internals match the entered command
					boolean pipe = false; //will use over loop to determine if there is a pipe
					int sp = 0;	//used to store the array index of the pipe.
					for(int x = 0; x < parsed.length; x++){ //loop to find if there is a pipe and store the location
						if(parsed[x].compareTo("|") == 0){
							pipe = true;
							sp = x;
						}
					}
					if(pipe){ //the case we need to pipe.
						ProcessBuilder[] pb = { //create 2 processbuilders in an array
							new ProcessBuilder(Arrays.copyOfRange(parsed, 0, sp)),
							new ProcessBuilder(Arrays.copyOfRange(parsed, sp + 1, parsed.length))};
						//change directories and input, output and errors as needed
						pb[0].directory(new File(Directory));
						pb[1].directory(new File(Directory));
						pb[0].redirectInput(ProcessBuilder.Redirect.INHERIT);
						pb[1].redirectOutput(ProcessBuilder.Redirect.INHERIT);
						pb[0].redirectError(ProcessBuilder.Redirect.INHERIT);
						pb[1].redirectError(ProcessBuilder.Redirect.INHERIT);
						try{ //try to run the processes
							long start = System.currentTimeMillis(); //used for timer
							List<Process> l = ProcessBuilder.startPipeline(Arrays.asList(pb));
							Process p = l.get(l.size() -1);
							(l.get(1)).waitFor(); //wait for the last process to end
							long finish = System.currentTimeMillis();
							time += (finish - start) / 1000f; //calculated the new time
						}
						catch(Exception e){
							System.out.println(e.toString());
							System.out.format("Command not found: %s\n", parsed[0]);
						}
					}
					else { //used if no pipe
						ProcessBuilder pb = new ProcessBuilder(parsed); //build a process
						//change input output error and directory
						pb.redirectInput(ProcessBuilder.Redirect.INHERIT);
						pb.redirectOutput(ProcessBuilder.Redirect.INHERIT);
						pb.directory(new File(Directory));
						pb.redirectError(ProcessBuilder.Redirect.INHERIT);
						try{
							long start = System.currentTimeMillis(); //used for timer
							Process p = pb.start(); //run the process and wait
							p.waitFor();
							long finish = System.currentTimeMillis();
							time += (finish - start) / 1000f; //calculate new time
						}catch(Exception e){
							System.out.format("Command not found: %s\n", parsed[0]);
						}

					}
			}
		}

	}
	//the following method was provided by Erik Falor to handle quoted command line argument and is his work
	//he has graciously let his class used it. I take no credit for it
	public String[] parse(String command){ 
		java.util.List<String> matchList = new java.util.ArrayList<>();
		Pattern regex = Pattern.compile("[^\\s\"']+|\"([^\"]*)\"|'([^']*)'");
		Matcher regexMatcher = regex.matcher(command);
		while (regexMatcher.find()) {
			if (regexMatcher.group(1) != null) {
			// Add double-quoted string without the quotes
				matchList.add(regexMatcher.group(1));
			} else if (regexMatcher.group(2) != null) {
			// Add single-quoted string without the quotes
				matchList.add(regexMatcher.group(2));
			} else {
			// Add unquoted word
				matchList.add(regexMatcher.group());
			}
		}
		return matchList.toArray(new String[matchList.size()]);
	}
}
