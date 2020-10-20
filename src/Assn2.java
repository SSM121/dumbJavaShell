import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.Scanner;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.nio.file.Files;

public class Assn2{
	static String Directory = "";
	public static void main(String[] args) throws IOException{
		Directory = System.getProperty("user.dir"); //get the starting user directory. all directory changes will be here
		Scanner inputStream = new Scanner(System.in);
		String inputString = "";
		while(true){ //the main shell loop
			System.out.format("[%s]: ", Directory); //display the current dir
			try{
				inputString = inputStream.nextLine(); //read a line of input
			} catch (Exception e){
				System.exit(0);
			}
			String[] parsed = parse(inputString);
			execute(parsed);
		}
	}

	public static void execute(String[] parsed){
		if(parsed.length > 0){ //used to make sure the user did not input an empty command. without this an empty command throws exception
			switch(parsed[0]){
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
					Directory = Builtins.cd(Directory, parsed[1]);
					break;
			}
		}

	}
	public static String[] parse(String command){
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
