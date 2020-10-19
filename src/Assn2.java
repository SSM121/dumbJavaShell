import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.Scanner;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
public class Assn2{
	public static void main(String[] args) throws IOException{
		String Directory = System.getProperty("user.dir"); //get the starting user directory. all directory changes will be here
		boolean flag = true;
		Scanner inputStream = new Scanner(System.in);
		while(flag){ //the main shell loop
			System.out.format("[%s]: ", Directory); //display the current dir
			String inputString = inputStream.nextLine(); //read a line of input
			String[] parsed = parse(inputString);
			System.out.println("\n" + parsed[0]);
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
