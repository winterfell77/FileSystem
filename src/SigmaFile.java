import java.io.*;
import java.util.*;

abstract class files{
  String name;
  int uniqueId;
  files parent;
  Boolean isfolder;

  public files(String name, int uniqueId){
    this.name = name;
    this.uniqueId = uniqueId;
  }

  public void setParent(files parent) {
    this.parent = parent;
  }
}

class document extends files{
  char documentType;

  public document(String name, int uniqueId, char filetype) {
    super(name, uniqueId);
    this.documentType = filetype;
    isfolder = false;
  }

}

class folder extends files{

  List<files> children = new ArrayList<>();

  public folder(String name, int uniqueId) {
    super(name, uniqueId);
    isfolder = true;
  }

  public String[] childrenNames(){
    String[] nameList = new String[children.size()];
    for(int i = 0 ;i< children.size();i++){
      nameList[i] = children.get(i).name;
    }
    return nameList;
  }
}


class SigmaFileSystem {

  Map<Integer,files> filesystem;
  private int totalDashboards = 0;
  private int totalWorksheets = 0;
  folder root;
  int possibleDepth=0;

  public SigmaFileSystem() {
    filesystem = new HashMap<>();
    root = new folder("MyDocuments",0);
    filesystem.put(0,root);
  }

  int getTotalDashboards() {
    return totalDashboards;
  }

  int getTotalWorksheets() {
    return totalWorksheets;
  }
  // I added the uniqueId parameter because a new file needs a uniqueid for itself
  void addNewFile(String fileName, String fileType,int uniqueId, int folderId) {
    possibleDepth++;
    if(fileType.equals("worksheet")){
      document newdocument = new document(fileName,uniqueId,'w');
      folder folderlocation = (folder) filesystem.get(folderId);
      newdocument.setParent(folderlocation);
      filesystem.put(uniqueId,newdocument);
      folderlocation.children.add(newdocument);
      totalWorksheets++;
    }
    if(fileType.equals("dashboard")){
      document newdocument = new document(fileName,uniqueId,'d');
      folder folderlocation = (folder) filesystem.get(folderId);
      newdocument.setParent(folderlocation);
      filesystem.put(uniqueId,newdocument);
      folderlocation.children.add(newdocument);
      totalDashboards++;
    }
    if(fileType.equals("folder")){
      folder newfolder = new folder(fileName,uniqueId);
      folder folderlocation = (folder) filesystem.get(folderId);
      newfolder.setParent(folderlocation);
      filesystem.put(uniqueId,newfolder);
      folderlocation.children.add(newfolder);
    }
  }

  int getFileId(String fileName, int folderId) {
    int fileid = -1;
    if(fileName.equals("MyDocuments")){ //although the instruction said not required, I assumed 0 for MyDocument
      return 0;
    }
    folder folderlocation = (folder) filesystem.get(folderId);
    for(files f:folderlocation.children){
      if(f.name.equals(fileName)){
        fileid = f.uniqueId;
        break;
      }
    }
    return fileid;
  }

  void moveFile(int fileId, int newFolderId) {
    files temp = filesystem.get(fileId);
    folder folderlocation = (folder) temp.parent;
    folderlocation.children.remove(temp);
    filesystem.remove(fileId);
    folder newfolderlocation = (folder) filesystem.get(newFolderId);
    newfolderlocation.children.add(temp);
    filesystem.put(fileId,temp);
  }

  String[] getFiles(int folderId) {
    folder folderlocation = (folder) filesystem.get(folderId);
    return folderlocation.childrenNames();
  }

  void printTree(files file, int depth, boolean[] flag, boolean isLast){
    for (int i = 1; i < depth; ++i) {
      if (flag[i]) {
        System.out.print("| " + " " + " " + " ");
      }
      else {
        System.out.print(" " + " " + " " + " ");
      }
    }

    if(depth == 0){
      System.out.println(file.name);
    }

    else if (isLast) {
      System.out.print("+--- " +  file.name + '\n');
      flag[depth] = false;
    }
    else {
      System.out.print("+--- " +  file.name + '\n');
    }

    int it = 0;
    if(file.isfolder){
      folder folder = (folder) file;
      for (files f : folder.children) {
        ++it;
        printTree(f, depth + 1, flag, it == (folder.children.size()) - 1);
      }
    }
    flag[depth] = true;
  }

  void printFiles() {
    boolean[] flag = new boolean[possibleDepth];
    Arrays.fill(flag, true);
    printTree(root, 0,flag, false);
  }

}
//TESTING SEQUENCE
public class SigmaFile {
  public static void main(String[] args) {
    /*
    SigmaFileSystem fs =new SigmaFileSystem();
    if(fs.getFileId("MyDocuments",0)==0){
      System.out.println("PASS");
    } else{
      System.out.println("FAIL");
    }

    fs.addNewFile("allen","folder",1,0);
    if(fs.getFileId("allen",0)==1){
      System.out.println("PASS");
    } else{
      System.out.println("FAIL");
    }

    fs.addNewFile("erin","worksheet",2,0);
    if(fs.getFileId("erin",0)==2){
      System.out.println("PASS");
    } else{
      System.out.println("FAIL");
    }
    fs.addNewFile("holly","dashboard",3,1);
    if(fs.getFileId("holly",1)==3){
      System.out.println("PASS");
    } else{
      System.out.println("FAIL");
    }
    fs.printFiles();
    fs.moveFile(3,0);
    if(fs.getFileId("holly",0)==3){
      System.out.println("PASS");
    } else{
      System.out.println("FAIL");
    }
    fs.printFiles();
    fs.addNewFile("katie","folder",4, 0);
    if(fs.getFileId("katie",0)==3){
      System.out.println("PASS");
    } else{
      System.out.println("FAIL");
    }
    fs.printFiles();
    String[] temp = {"allen","erin","holly","katie"};
    if(Arrays.equals(fs.getFiles(0), temp)){
      System.out.println("PASS");
    } else{
      System.out.println("FAIL");
    }
    fs.moveFile(4,1);
    fs.addNewFile("christianna","folder",5,0);
    fs.addNewFile("computer","dashboard",6,5);
    fs.moveFile(5,1);
    fs.addNewFile("laptop","worksheet",7,5);
    fs.addNewFile("erin","worksheet",8,4);
    fs.printFiles();

    if(fs.getTotalDashboards()==2){
      System.out.println("PASS");
    } else{
      System.out.println("FAIL");
    }
    if(fs.getTotalWorksheets()==2){
      System.out.println("PASS");
    } else{
      System.out.println("FAIL");
    }
*/
    SigmaFileSystem fs = new SigmaFileSystem();
    int rootId = fs.getFileId("MyDocuments", 0);
    System.out.println(rootId);
    fs.addNewFile("draft", "folder", 1, rootId);            //i added a paramter of the unique id because i noticed that there wasn't a paarameter for it
    fs.addNewFile("complete", "folder", 2, rootId);
    int draftId = fs.getFileId("draft", rootId);
    int completeId = fs.getFileId("complete", rootId);
    fs.addNewFile("foo", "worksheet", 3, draftId);
    fs.addNewFile("bar", "dashboard", 4, completeId);
    int fooId = fs.getFileId("foo", draftId);
    fs.moveFile(fooId, completeId);

    System.out.println(String.join(", ", fs.getFiles(rootId)));
    System.out.println(String.join(", ", fs.getFiles(draftId)));
    System.out.println(String.join(", ", fs.getFiles(completeId)));

    fs.addNewFile("project", "folder",9,  draftId);
    int projectId = fs.getFileId("project", draftId);
    fs.addNewFile("page1", "worksheet", 5, projectId);
    fs.addNewFile("page2", "worksheet", 6, projectId);
    fs.addNewFile("page3", "worksheet", 7,  projectId);
    fs.addNewFile("cover", "dashboard", 8, projectId);
    fs.moveFile(projectId, completeId);
    projectId = fs.getFileId("project", completeId);
    int coverId = fs.getFileId("cover", projectId);
    fs.moveFile(coverId, rootId);

    System.out.println(String.join(", ", fs.getFiles(rootId)));
    System.out.println(String.join(", ", fs.getFiles(draftId)));
    System.out.println(String.join(", ", fs.getFiles(completeId)));
    System.out.println(String.join(", ", fs.getFiles(projectId)));

    System.out.println(fs.getTotalDashboards());
    System.out.println(fs.getTotalWorksheets());
    fs.printFiles();
/*
    boolean running = true;
    Scanner scanner = new Scanner(System.in);
    SigmaFileSystem fs = new SigmaFileSystem();
    int command;
    while (running) {
      command = askForInteger(scanner, "\nEnter an integer to indicate a command: \n[1] get_total_dashboards\n[2] get_total_worksheets\n[3] add_new_folder\n[4] get_file_id\n[5] move_file\n[6] get_files \n[7] print_files\n[8] exit\n");
      switch (command) {
        case 1: {
          int totalDashboards = fs.getTotalDashboards();
          System.out.println(String.format("There are %d dashboards in the file system.", totalDashboards));
          break;
        }
        case 2: {
          int totalWorksheets = fs.getTotalWorksheets();
          System.out.println(String.format("There are %d worksheets in the file system.", totalWorksheets));
          break;
        }
        case 3: {
          System.out.println("Enter a new file name:");
          String fileName = scanner.nextLine();
          System.out.println("Enter a file type (worksheet, dashboard, or folder)");
          String fileType = scanner.nextLine();
          int folderId = askForInteger(scanner, "Enter a folder id where you'd like to put this file");
          fs.addNewFile(fileName, fileType, folderId);
          System.out.println(String.format("%s has been added to folder %d", fileName, folderId));
          break;
        }
        case 4: {
          System.out.println("Enter a file name:");
          String fileName = scanner.nextLine();
          int folderId = askForInteger(scanner, "Enter a folder id:");
          int fileId = fs.getFileId(fileName, folderId);
          System.out.println(String.format("%d is file %d", fileName, fileId));
          break;
        }
        case 5: {
          int fileId = askForInteger(scanner, "Enter a file id:");
          int newFileId = askForInteger(scanner, "Enter the folder id where you'd like to move this file.");
          fs.moveFile(fileId, newFileId);
          System.out.println(String.format("Successfully moved file %d to folder %d", fileId, newFileId));
          break;
        }
        case 6: {
          int folderId = askForInteger(scanner, "Enter a folder id:");
          String[] fileNames = fs.getFiles(folderId);
          if (fileNames.length == 0) {
            System.out.println(String.format("There are no files in folder %d", folderId));
          } else {
            System.out.println(String.format("The following files are in folder %d:", folderId));
            for (String fileName: fileNames) {
              System.out.println(String.format("\t%s", fileName));
            }
          }
          break;
        }
        case 7: {
          fs.printFiles();
          break;
        }
        case 8: {
          System.out.println("Exiting program.");
          running = false;
          scanner.close();
          break;
        }
        default:
          System.out.println(String.format("Invalid command: %d. Please try again.\n",command));
      }
    }
    */
  }
}
