package com.crio.qcharm.ds;

import com.crio.qcharm.request.EditRequest;
import com.crio.qcharm.request.PageRequest;
import com.crio.qcharm.request.SearchReplaceRequest;
import com.crio.qcharm.request.SearchRequest;
import com.crio.qcharm.request.UndoRequest;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;
import java.util.stream.Collectors;

public class SourceFileHandlerLinkedListImpl implements SourceFileHandler {
  SourceFileVersion obj;
  SourceFileVersion ver;
  List<String> list = new LinkedList<String>();
  List<String> lines = new LinkedList<String>();


  public SourceFileHandlerLinkedListImpl(String testfile) {
  }



  // TODO: CRIO_TASK_MODULE_IMPROVING_EDITS
  // Input:
  //      FileName
  //  Steps:
  //    1. Given SourceFile name get the latest version of the it.
  //  Description:
  //    After loading the file the SourceFile would have gone through multiple
  //    changes. When we say "Latest version of the SourceFile" it means the SourceFile's present
  //    view after applying all the changes.

  @Override
  public SourceFileVersion getLatestSourceFileVersion(String fileName) {
    SourceFileVersion file = new SourceFileVersionArrayListImpl(fileName);
    return file;
  }

  // TODO: CRIO_TASK_MODULE_IMPROVING_EDITS
  // Input:
  //      FileInfo
  //  Steps:
  //    1. Create an object of the SourceFileVersionLinkedListImpl class using the given fileInfo.
  //    2. Using that object get the first 50 lines of this file.
  //    3. Create Page object using the lines received and return the same.
  //  How to construct Page object ?
  //    1. lines should be the first 50 lines of the SourceFile
  //    2. cursorAt should be new Cursor(0,0)
  //    3. StartingLineNo is set to 0
  //    4. fileName should be same as the fileInfo.fileName
  //
  //  What is Cursor?
  //     It represents position of the cursor in the editor.
  //     Cursor is represented using (lineNumber, columnNumber).

  @Override
  public Page loadFile(FileInfo fileInfo) {
    obj = new SourceFileVersionArrayListImpl(fileInfo);

    
    List<String>subList = new LinkedList<String>(); 
    lines = obj.getAllLines();
    int t = lines.size();
    if(t <  50){
      subList = lines.subList(0, t);
    }
    else{
      subList = lines.subList(0,50);
    }
    
    Cursor cursor = new Cursor(0,0);
    Page p = new Page(subList, 0, fileInfo.fileName, cursor);
    return p;
  }
  

  // TODO: CRIO_TASK_MODULE_IMPROVING_EDITS
  // Input:
  //     PageRequest - contains following information
  //         1. Starting line number
  //         2. File name;
  //         3. requested number of Lines
  //         4. Cursor position
  //  Steps:
  //    1. After loadFile SourceFileVersionLinkedListImpl has all the file information
  //    2. Using that get "requested number of lines" above "the given line number".
  //    3. Construct Page object and return
  //  How to construct Page object ?
  //    1. lines - lines you got in step 2
  //    2. cursorAt should be same as pageRequest.cursorAt
  //    3. StartingLineNo should be same as first line number of lines
  //    4. fileName should be same as the pageRequest.fileName

  @Override
  public Page getPrevLines(PageRequest pageRequest) {
    Page p = new Page();
    p = obj.getLinesBefore(pageRequest);
    return p;
  }

  // TODO: CRIO_TASK_MODULE_IMPROVING_EDITS
  // Input:
  //     PageRequest - contains following information
  //         1. Starting line number
  //         2. File name;
  //         3. requested number of Lines
  //         4. Cursor position
  //  Steps:
  //    1. After loadFile SourceFileVersionLinkedListImpl has all the file information
  //    2. Using that get "requested number of lines" below "the given line number".
  //    3. Construct Page object and return
  //  How to construct Page object ?
  //    1. lines - lines you got in step 2
  //    2. cursorAt should be same as pageRequest.cursorAt
  //    3. StartingLineNo should be same as first line number of lines
  //    4. fileName should be same as the pageRequest.fileName

  @Override
  public Page getNextLines(PageRequest pageRequest) {
    Page p = new Page();
    p = obj.getLinesAfter(pageRequest);
    return p;
  }

  // TODO: CRIO_TASK_MODULE_IMPROVING_EDITS
  // Input:
  //     PageRequest - contains following information
  //         1. Starting line number
  //         2. File name;
  //         3. requested number of Lines
  //         4. Cursor position
  //  Steps:
  //    1. After loadFile SourceFileVersionLinkedListImpl has all the file information
  //    2. Using the object get "requested number of lines" starting from "the given line number".
  //    3. Construct Page object and return
  //  How to construct Page object ?
  //    1. lines - lines you got in step 2
  //    2. cursorAt should be same be set to (startingLineNo, 0);
  //    3. StartingLineNo should be same as first line number of lines
  //    4. fileName should be same as the pageRequest.fileName

  @Override
  public Page getLinesFrom(PageRequest pageRequest) {
    Page p = new Page();
    p = obj.getLinesFrom(pageRequest);
    return p;
  }

  // TODO: CRIO_TASK_MODULE_IMPROVING_EDITS
  // Input:
  //     SearchRequest - contains following information
  //         1. pattern - pattern you want to search
  //         2. File name - file where you want to search for the pattern
  // Description:
  //    1. For the given SourceFile use SourceFileVersionLinkedListImpl
  //    .getCursors() to find all occurrences of the pattern in the SourceFile.
  //    2. return the all occurrences starting position in a list.

  @Override
  public List<Cursor> search(SearchRequest searchRequest) {
    return obj.getCursors(searchRequest);
  }

  // TODO: CRIO_TASK_MODULE_IMPROVING_EDITS
  // Input:
  //     CopyBuffer - contains following information
  //         1. List of lines
  // Description:
  //      Store the incoming copy buffer

  @Override
  public void setCopyBuffer(CopyBuffer copyBuffer) {
    list = copyBuffer.getLines();
  }

  // TODO: CRIO_TASK_MODULE_IMPROVING_EDITS
  // Input:
  //      None
  // Description:
  //      return the previously stored copy buffer
  //      if nothing is stored return copy buffer containing empty lines.

  @Override
  public CopyBuffer getCopyBuffer() {
    CopyBuffer c = new CopyBuffer(list);
    return c;
  }

  // TODO: CRIO_TASK_MODULE_IMPROVING_EDITS
  // Input:
  //      Object of type SourceFileVersionLinkedListImpl
  // Description:
  //      make a copy of the the given SourceFileVersionLinkedListImpl object return new object
  // NOTE:
  //      DON'T CHANGE THE SIGNATURE OF THIS FUNCTION

  @Override
  public SourceFileVersion cloneObj(SourceFileVersion ver) {
    this.ver = ver ;
    return ver;
  }

  // TODO: CRIO_TASK_MODULE_IMPROVING_EDITS
  // Input:
  //     EditRequest
  //        1. starting line no - starting line number of last time it received page from backend
  //        2. ending line no - ending line no of the last time it received page from backend;
  //        3. new content - list of lines present view of lines(starting line no, ending line no)
  //        4. file name
  //        5. cursor
  // Description:
  //        1. Remove the line numbers in the range(starting line no, ending line no)
  //        2. Inserting the lines in new content starting position starting line no
  // Example:
  //        EditRequest looks like this
  //            1. start line no - 50
  //            2. ending line no - 60
  //            3. new content - ["Hello world"]
  //
  //       Assume the file has 100 lines in it
  //
  //       File contents before edit:
  //       ==========================
  //       line no 1
  //       line no 2
  //          .....
  //       line no 100
  //
  //        File contents After Edit:
  //        =========================
  //        line no 1
  //        line no 2
  //        line no 3
  //         .....
  //        line no 49
  //        Hello World
  //        line no 61
  //        line no 62
  //          ....
  //        line no 100
  //

  @Override
  public void editLines(EditRequest editRequest) {
    int start = editRequest.getStartingLineNo();
    int end = editRequest.getEndingLineNo();
    List<String> content = editRequest.getNewContent();
    String name = editRequest.getFileName();
    Cursor cursor = editRequest.getCursorAt();
    lines.subList(start, end).clear();
    int j = 0;
    while(j < content.size() ) {
      lines.add(start, content.get(j));
      j++;
      start++;
    }  
  }




  


  // TODO: CRIO_TASK_MODULE_SEARCH_REPLACE
  // Input:
  //      SearchReplaceRequest
  //        1. pattern  - pattern to be found
  //        2. newPattern - pattern to be replaced with
  //        3. fileName
  // Description:
  //      using the SourceFileVersionLinkedListImpl object find every occurrence of pattern
  //      and replace it with the given newPattern

  @Override
  public void searchReplace(SearchReplaceRequest searchReplaceRequest) {
    int startingLineNo = searchReplaceRequest.getStartingLineNo();
    int length = searchReplaceRequest.getLength();
    Cursor cursor = new Cursor(0,0);
    String pattern = searchReplaceRequest.getPattern();
    String newPattern = searchReplaceRequest.getNewPattern();
    SearchReplace s = new SearchReplace(startingLineNo, length, cursor, pattern, newPattern);
    obj.apply(s);
  }







}
