import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.DirectoryStream;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.lang.StringBuffer;
import java.text.FieldPosition;
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

}
