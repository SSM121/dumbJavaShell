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
	float time = 0.0f;
	String Directory = "";
	ArrayList<String> History = new ArrayList<String>();
	public Shell(String[] args) throws IOException{
		Directory = System.getProperty("user.dir"); //get the starting user directory. all directory changes will be here
		Scanner inputStream = new Scanner(System.in);
		String inputString = "";
		while(true){ //the main shell loop
			System.out.format("[%s]: ", Directory); //display the current dir
			try{
				inputString = inputStream.nextLine(); //read a line of input
				History.add(inputString);
			} catch (Exception e){
				System.exit(0);
			}
			String[] parsed = parse(inputString);
			execute(parsed);
		}
	}

	public void execute(String[] parsed){
		if(parsed.length > 0){ //used to make sure the user did not input an empty command. without this an empty command throws exception
			switch(parsed[0]){
				case "ptime":
					System.out.format("Total time in child processex: %.4f\n", time);
					break;
				case "here":
					Builtins.here(Directory);
					break;
				case "exit":
					System.exit(0);
					break;
				case "list":
					Builtins.list(Directory);
					break;
				case "cd":
					if(parsed.length > 1)
						Directory = Builtins.cd(Directory, parsed[1]);
					else if(parsed.length == 1)
						Directory = System.getProperty("user.home");
					break;
				case "mdir":
					if(parsed.length > 1)
						Builtins.mdir(Directory, parsed[1]);
					break;
				case "rdir":
					if(parsed.length > 1)
						Builtins.rdir(Directory + "/" + parsed[1]);
					break;
				case "history":
					Builtins.history(History);
					break;
				case "^":
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
				default:
					boolean pipe = false; //will use over loop to determine if there is a pipe
					int sp = 0;	//used to store the array index of the pipe.
					for(int x = 0; x < parsed.length; x++){
						if(parsed[x].compareTo("|") == 0){
							pipe = true;
							sp = x;
						}
					}
					if(pipe){ //the case we need to pipe.
						ProcessBuilder[] pb = {
							new ProcessBuilder(Arrays.copyOfRange(parsed, 0, sp)),
							new ProcessBuilder(Arrays.copyOfRange(parsed, sp + 1, parsed.length))};
						pb[0].directory(new File(Directory));
						pb[1].directory(new File(Directory));
						pb[0].redirectInput(ProcessBuilder.Redirect.INHERIT);
						pb[1].redirectOutput(ProcessBuilder.Redirect.INHERIT);
						pb[0].redirectError(ProcessBuilder.Redirect.INHERIT);
						pb[1].redirectError(ProcessBuilder.Redirect.INHERIT);
						try{
							long start = System.currentTimeMillis();
							List<Process> l = ProcessBuilder.startPipeline(Arrays.asList(pb));
							Process p = l.get(l.size() -1);
							(l.get(1)).waitFor();
							long finish = System.currentTimeMillis();
							time += (finish - start) / 1000f;
						}
						catch(Exception e){
							System.out.println(e.toString());
							System.out.format("Command not found: %s\n", parsed[0]);
						}
					}
					else { //used if no pipe
						ProcessBuilder pb = new ProcessBuilder(parsed);
						pb.redirectInput(ProcessBuilder.Redirect.INHERIT);
						pb.redirectOutput(ProcessBuilder.Redirect.INHERIT);
						pb.directory(new File(Directory));
						pb.redirectError(ProcessBuilder.Redirect.INHERIT);
						try{
							long start = System.currentTimeMillis();
							Process p = pb.start();
							p.waitFor();
							long finish = System.currentTimeMillis();
							time += (finish - start) / 1000f;
						}catch(Exception e){
							System.out.format("Command not found: %s\n", parsed[0]);
						}

					}
			}
		}

	}
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
