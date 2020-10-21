import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.DirectoryStream;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.lang.StringBuffer;
import java.text.FieldPosition;
import java.util.ArrayList;

public class Builtins{
	public static void here(String Directory){
		System.out.format("%s\n", Directory);
	}
	public static void list(String Directory){
		Path p = (new File(Directory)).toPath();
		try (DirectoryStream<Path> stream = Files.newDirectoryStream(p)) {
			for (Path entry: stream) {
				StringBuffer s = new StringBuffer();
				if(Files.isDirectory(entry)){
					s.append('d');
				}else{
					s.append('-');
				}
				if(Files.isReadable(entry)){
					s.append('r');
				}else{
					s.append('-');
				}
				if(Files.isWritable(entry)){
					s.append('w');
				}else{
					s.append('-');
				}
				if(Files.isExecutable(entry)){
					s.append('x');
				}else{
					s.append('-');
				}
				s.append(String.format("%10d", Files.size(entry)));
				Date d = new Date(Files.getLastModifiedTime(entry).toMillis());
				SimpleDateFormat f = new SimpleDateFormat(" MMM dd, yyyy HH:mm ");
				f.format(d, s, new FieldPosition(0));
				s.append((entry.getFileName()).toString());
				System.out.println(s);
			}
		}
		catch(Exception e){
			System.out.println("test2");
		}
	}
	public static String cd(String Directory, String goal){
		if(goal.compareTo("..") == 0){
			Path p = (new File(Directory)).toPath();
			Path z = p.getParent();
			return z.toString();
		}
		else if(goal.compareTo(".") == 0){
			return Directory;
		}
		else{
			Path p = (new File(Directory + "/" + goal)).toPath();
			if(Files.exists(p) && Files.isDirectory(p)){
				return p.toString();
			}
			else{
				System.out.format("Error: %s is not a valid directory in %s\n", goal, Directory);
			}


		}
		return Directory;
	}

	public static void mdir(String Directory, String goal){
		Path p = (new File(Directory + "/" + goal)).toPath();
		if(Files.exists(p)){
			if(Files.isDirectory(p)){
				System.out.format("The Directory %s already exsists!\n", goal);
				return;
			}
			else
			{
				System.out.format("A file named %s already exsists \n", goal);
				return;
			}

		}
		else
		{
			try{
				Files.createDirectory(p);
			} catch(Exception e){
				System.out.println("Unexpected error occurred. creation failed");
			}
		}
	}

	public static void rdir(String Directory){ //send in goal concatenated with Directory so this can be recursive
		Path p = (new File(Directory)).toPath();
		if(Files.exists(p))
		{
			if(Files.isDirectory(p)){
				try (DirectoryStream<Path> stream = Files.newDirectoryStream(p)) {
					for (Path entry: stream) {
						rdir(entry.toString());
					}
				} catch (Exception e){}
			}
			try{
				Files.delete(p);
			}catch (Exception e){}
		}
		
	}

	public static void history(ArrayList<String> History){
		for(int i = 0; i < History.size(); i++){
			System.out.format("%d : %s\n", i + 1, History.get(i));
		}
	}


}
