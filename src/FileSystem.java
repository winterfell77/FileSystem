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


class FileSystem {

  Map<Integer,files> filesystem;
  private int totalDashboards = 0;
  private int totalWorksheets = 0;
  folder root;
  int possibleDepth=0;

  public FileSystem() {
    filesystem = new HashMap<>();
    root = new folder("MyDocuments",0);  //assumption root is mydocuments and 0 as uniqueid
    filesystem.put(0,root);
  }

  int getTotalDashboards() {
    return totalDashboards;
  }

  int getTotalWorksheets() {
    return totalWorksheets;
  }
  // I added the uniqueId parameter because a new file needs a uniqueid for itself
  //assumption: the filetype is either worksheet, dashboard, or folder
  //assumption: the folderId exists
  void addNewFile(String fileName, String fileType,int uniqueId, int folderId) {
    possibleDepth++;
    if(fileType.equals("worksheet")){
      document newDocument = new document(fileName,uniqueId,'w');
      folder folderlocation = (folder) filesystem.get(folderId);
      newDocument.setParent(folderlocation);
      filesystem.put(uniqueId,newDocument);
      folderlocation.children.add(newDocument);
      totalWorksheets++;
    }
    if(fileType.equals("dashboard")){
      document newDocument = new document(fileName,uniqueId,'d');
      folder folderlocation = (folder) filesystem.get(folderId);
      newDocument.setParent(folderlocation);
      filesystem.put(uniqueId,newDocument);
      folderlocation.children.add(newDocument);
      totalDashboards++;
    }
    if(fileType.equals("folder")){
      folder newFolder = new folder(fileName,uniqueId);
      folder folderlocation = (folder) filesystem.get(folderId);
      newFolder.setParent(folderlocation);
      filesystem.put(uniqueId,newFolder);
      folderlocation.children.add(newFolder);
    }
  }
  //assumption: folderId exists and the fileName exists in that folder
  //allows for duplicate filenames but in different/unique folders
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

  //assumption: fileId exists
  //assumption: newfolderId exists
  void moveFile(int fileId, int newFolderId) {
    files temp = filesystem.get(fileId);
    folder folderlocation = (folder) temp.parent; //go into the folder which is the parent
    folderlocation.children.remove(temp);
    filesystem.remove(fileId);
    folder newfolderlocation = (folder) filesystem.get(newFolderId);
    newfolderlocation.children.add(temp);
    filesystem.put(fileId,temp);
  }

  //assumption: folderId exists
  String[] getFiles(int folderId) {
    folder folderlocation = (folder) filesystem.get(folderId);
    return folderlocation.childrenNames();
  }

  //helper recursive function
  void printTree(files file, int depth, boolean isLast){
    for (int i = 1; i < depth; ++i) {
      System.out.print("│     ");
    }
    if(depth == 0){
      System.out.println(file.name+"("+file.uniqueId+")");
    }
    else if (isLast) {
      System.out.print("├─── " +  file.name+"("+file.uniqueId+")" + '\n');
    }
    else {
      System.out.print("├─── " +  file.name+"("+file.uniqueId+")" + '\n');
    }
    int it = 0;
    //traverse to its children
    if(file.isfolder){
      folder folder = (folder) file;
      for (files f : folder.children) {
        ++it;
        printTree(f, depth + 1, it == (folder.children.size()) - 1);
      }
    }
  }

  //assumption: print file structure by its name and uniqueId
  void printFiles() {
    printTree(root, 0, false);
  }

}


class Solution {

  private static int askForInteger(Scanner scanner, String question) {
    System.out.println(question);
    try {
      return Integer.parseInt(scanner.nextLine());
    } catch (Exception e) {
      System.out.println("Please enter a valid integer.");
      return askForInteger(scanner, question);
    }
  }

  public static void runExample() {

    // compared output against expected output
    FileSystem fs = new FileSystem();
    int rootId = fs.getFileId("MyDocuments", 0);
    fs.addNewFile("draft", "folder", 1, rootId);           //i added a paramter of the unique id because i noticed that there wasn't a paarameter for it
    fs.addNewFile("complete", "folder", 2, rootId);    //added unique id parameter
    int draftId = fs.getFileId("draft", rootId);  //1
    int completeId = fs.getFileId("complete", rootId);  //2
    fs.addNewFile("foo", "worksheet", 3, draftId);                 //added unique id parameter
    fs.addNewFile("bar", "dashboard", 4, completeId);              //added unique id parameter
    int fooId = fs.getFileId("foo", draftId);  //3
    fs.moveFile(fooId, completeId);

    System.out.println(String.join(", ", fs.getFiles(rootId)));  //draft,complete
    System.out.println(String.join(", ", fs.getFiles(draftId))); // (nothing)
    System.out.println(String.join(", ", fs.getFiles(completeId)));  //bar,foo

    fs.addNewFile("project", "folder",9,  draftId);                  //added unique id parameter
    int projectId = fs.getFileId("project", draftId);              //9
    fs.addNewFile("page1", "worksheet", 5, projectId);              //added unique id parameter
    fs.addNewFile("page2", "worksheet", 6, projectId);               //added unique id parameter
    fs.addNewFile("page3", "worksheet", 7,  projectId);                  //added unique id parameter
    fs.addNewFile("cover", "dashboard", 8, projectId);              //added unique id parameter
    fs.moveFile(projectId, completeId);
    projectId = fs.getFileId("project", completeId); //9
    int coverId = fs.getFileId("cover", projectId);   //8
    fs.moveFile(coverId, rootId);

    System.out.println(String.join(", ", fs.getFiles(rootId)));  //draft,complete, cover
    System.out.println(String.join(", ", fs.getFiles(draftId))); //nothing
    System.out.println(String.join(", ", fs.getFiles(completeId))); //bar, foo, project
    System.out.println(String.join(", ", fs.getFiles(projectId))); // page1, page2, page 3

    System.out.println(fs.getTotalDashboards());  //2
    System.out.println(fs.getTotalWorksheets());  //4
    fs.printFiles();
  }


  public static void main(String[] args) {

    boolean running = true;
    Scanner scanner = new Scanner(System.in);
    FileSystem fs = new FileSystem();
    int command;
    while (running) {
      command = askForInteger(scanner, "\nEnter an integer to indicate a command: \n[1] get_total_dashboards\n[2] get_total_worksheets\n[3] add_new_file\n[4] get_file_id\n[5] move_file\n[6] get_files \n[7] print_files\n[8] exit\n");         //I changed it to from add new folder to add new file, because the function should be able to add any file
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
          int uniqueId = askForInteger(scanner, "What would be the unique id for the file?");     //added the unique id ask to fill the parameter
          int folderId = askForInteger(scanner, "Enter a folder id where you'd like to put this file");
          fs.addNewFile(fileName, fileType, uniqueId, folderId);
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
  }
}
