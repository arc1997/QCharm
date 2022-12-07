package com.crio.qcharm.ds;

import com.crio.qcharm.request.PageRequest;
import com.crio.qcharm.request.SearchRequest;

import org.apache.commons.lang3.StringUtils;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class SourceFileVersionLinkedListImpl implements SourceFileVersion,Cloneable {
  String name;
  List<String> lines = new LinkedList<String>();
  SourceFileVersion obj;




  


  // TODO: CRIO_TASK_MODULE_IMPROVING_EDITS
  // Input:
  //     FileInfo - contains following information
  //         1. fileName
  //         2. List of lines
  // Steps:
  //     You task here is to construct SourceFileVersionLinkedListImpl object by
  //     1. Storing the lines received from fileInfo object
  //     2. Storing the fileName received from fileInfo object.
  // Recommendations:
  //     1. Use Java LinkedList to store the lines received from fileInfo


  public SourceFileVersionLinkedListImpl(FileInfo fileInfo) throws CloneNotSupportedException {
    this.name = fileInfo.getFileName();
    List<String>lines1 = fileInfo.getLines();
    for(int i = 0;i < lines1.size();i++) {
      lines.add(new String(lines1.get(i)));
    }
  }


  

  public SourceFileVersionLinkedListImpl() {
  }

  public SourceFileVersionLinkedListImpl(SourceFileVersionLinkedListImpl obj) {
    this.obj = obj;

  }


  @Override
  public SourceFileVersion apply(List<Edits> edits) {
    List<String> lines = new LinkedList<>();
    lines.addAll(lines);

    SourceFileVersionLinkedListImpl latest = new SourceFileVersionLinkedListImpl();

    for (Edits oneEdit : edits) {
      if (oneEdit instanceof UpdateLines) {
        apply((UpdateLines) oneEdit);
      } else {
        assert (oneEdit instanceof SearchReplace);
        apply((SearchReplace) oneEdit);
      }
    }
    return this;
  }


  // TODO: CRIO_TASK_MODULE_SEARCH_REPLACE
  // Input:
  //    SearchReplace
  //          1. pattern - pattern to be found
  //          2. newPattern - pattern to be replaced with
  //  Description:
  //      Find every occurrence of the pattern and replace it newPattern.

  @Override
  public void apply(SearchReplace searchReplace) {
    String pattern = searchReplace.getPattern();
    String newPattern = searchReplace.getNewPattern();
    for(int i = 0; i < lines.size(); i++)
    {
      String text = lines.get(i);
      int m = pattern.length();
      int n = text.length();
      int lps[] = new int[m];
      int j=0;
      computeLPSarray(pattern, m, lps);
      int k=0;
      while(k < n) {
        if(pattern.charAt(j) == text.charAt(k)) {
          j++;
          k++;
        }
        if(j == m) {
          String r = text.substring(k-j, k-j+m);
          String ch = StringUtils.replaceAll(text, r, newPattern);
          lines.remove(i);
          lines.add(i,ch);
          
          j = lps[j - 1];
        }
        else if (k < n && pattern.charAt(j) != text.charAt(k)) {
          if(j != 0) {
            j = lps[j - 1];
          }
          else {
            k = k + 1;
          }
        }
      }

      
    }
  }

  // TODO: CRIO_TASK_MODULE_IMPROVING_EDITS
  // Input:
  //     UpdateLines
  //        1. startingLineNo - starting line number of last time it received page from backend
  //        2. numberOfLines - number of lines received from backend last time.
  //        3. lines - present view of lines in range(startingLineNo,startingLineNo+numberOfLines)
  //        4. cursor
  // Description:
  //        1. Remove the line numbers in the range(starting line no, ending line no)
  //        2. Inserting the lines in new content starting position starting line no
  // Example:
  //        UpdateLines looks like this
  //            1. start line no - 50
  //            2. numberOfLines - 10
  //            3. lines - ["Hello world"]
  //
  //       Assume the file has 100 lines in it
  //
  //       File contents before edit:
  //       ==========================
  //       line no 0
  //       line no 1
  //       line no 2
  //          .....
  //       line no 99
  //
  //        File contents After Edit:
  //        =========================
  //        line no 0
  //        line no 1
  //        line no 2
  //        line no 3
  //         .....
  //        line no 49
  //        Hello World
  //        line no 60
  //        line no 61
  //          ....
  //        line no 99
  //

  @Override
  public void apply(UpdateLines updateLines) {
    int start = updateLines.getStartingLineNo();
    int numberOfLines = updateLines.getNumberOfLines();
    int end = start + numberOfLines;
    List<String> content = updateLines.getLines();
    Cursor cursor = updateLines.getCursor();
    lines.subList(start, end).clear();
    int j = 0;
    while(j < content.size() ) {
      lines.add(start, content.get(j));
      j++;
      start++;
    }  
  }

  @Override
  public List<String> getAllLines() {
    List<String> s = new LinkedList<String>();
    s = lines;
    return s;
  }

  // TODO: CRIO_TASK_MODULE_IMPROVING_EDITS
  // Input:
  //    1. lineNumber - The line number
  //    2. numberOfLines - Number of lines requested
  // Expected functionality:
  //    1. Get the requested number of lines starting before the given line number.
  //    2. Make page object of this and return.
  //    3. For cursor position use the value from pageRequest
  //    4. For fileName use the value from pageRequest
  // NOTE:
  //    If there less than requested number of lines, then return just those lines.
  //    Zero is the first line number of the file
  // Example:
  //    lineNumber - 50
  //    numberOfLines - 25
  //    Then lines returned is
  //    (line number 25, line number 26 ... , line number 48, line number49)
  @Override
  public Page getLinesBefore(PageRequest pageRequest) {
    int lineNumber = pageRequest.getStartingLineNo();
    int numberOfLines = pageRequest.getNumberOfLines();
    int linesno = lineNumber - numberOfLines;
    List<String>subList = new LinkedList<String>(); 
    if(linesno > 0){
      subList = lines.subList(linesno,lineNumber-1);
    }
    else{
      subList = lines.subList(0,lineNumber);

    }
    Page p = new Page(subList,0, pageRequest.getFileName(), pageRequest.getCursorAt());
    return p;

  }

  // TODO: CRIO_TASK_MODULE_IMPROVING_EDITS
  // Input:
  //    1. lineNumber - The line number
  //    2. numberOfLines - Number of lines requested
  // Expected functionality:
  //    1. Get the requested number of lines starting after the given line number.
  //    2. Make page object of this and return.
  //    3. For cursor position use the value from pageRequest
  //    4. For fileName use the value from pageRequest
  // NOTE:
  //    If there less than requested number of lines, then return just those lines.
  //    Zero is the first line number of the file  @Override
  // Example:
  //    lineNumber - 50
  //    numberOfLines - 25
  //    Then lines returned is
  //    (line number 51, line number 52 ... , line number 74, line number75)

  @Override
  public Page getLinesAfter(PageRequest pageRequest) {
    int lineNumber = pageRequest.getStartingLineNo();
    int numberOfLines = pageRequest.getNumberOfLines();
    int linesno = lineNumber + numberOfLines+1;
    List<String>subList = new LinkedList<String>();
    Page p;
    int l = lines.size();
    if(linesno < l) {
      subList = lines.subList(lineNumber+1,linesno);
      p = new Page(subList, lineNumber+1, pageRequest.getFileName(), pageRequest.getCursorAt());
    }
    else if(lineNumber == l) {
      subList = lines.subList(lineNumber,l);
      p = new Page(subList, lineNumber, pageRequest.getFileName(), pageRequest.getCursorAt());
    }
    else {
      subList = lines.subList(lineNumber+1,l);
      p = new Page(subList, lineNumber+1, pageRequest.getFileName(), pageRequest.getCursorAt()); 
    }
    
    return p;
  }

  // TODO: CRIO_TASK_MODULE_IMPROVING_EDITS
  // Input:
  //    1. lineNumber - The line number
  //    2. numberOfLines - Number of lines requested
  // Expected functionality:
  //    1. Get the requested number of lines starting from the given line number.
  //    2. Make page object of this and return.
  //    3. For cursor position should be (startingLineNo, 0)
  //    4. For fileName use the value from pageRequest
  // NOTE:
  //    If there less than requested number of lines, then return just those lines.
  //    Zero is the first line number of the file  @Override
  // Example:
  //    lineNumber - 50
  //    numberOfLines - 25
  //    Then lines returned is
  //    (line number 50, line number 51 ... , line number 73, line number74)

  @Override
  public Page getLinesFrom(PageRequest pageRequest) {
    int lineNumber = pageRequest.getStartingLineNo();
    int numberOfLines = pageRequest.getNumberOfLines();
    int linesno = lineNumber + numberOfLines;
    List<String>subList = new LinkedList<String>(); 
    int l = lines.size();
    if(linesno < l){
      subList = lines.subList(lineNumber,linesno);
    }
    else{
      subList = lines.subList(lineNumber,l);

    }
    Cursor cursor = new Cursor(lineNumber, 0);
    Page p = new Page(subList, lineNumber, pageRequest.getFileName(), cursor);
    return p;
  }

  // TODO: CRIO_TASK_MODULE_IMPROVING_EDITS
  // Input:
  //    SearchRequest - contains following information
  //        1. pattern - pattern you want to search
  //        2. File name - file where you want to search for the pattern
  // Description:
  //    1. Find all occurrences of the pattern in the SourceFile
  //    2. Create an empty list of cursors
  //    3. For each occurrence starting position add to the list of cursors
  //    4. return the list of cursors
  // Recommendation:
  //    1. Use FASTER string search algorithm.
  //    2. Feel free to try any other algorithm/data structure to improve search speed.
  // Reference:
  //     https://www.geeksforgeeks.org/kmp-algorithm-for-pattern-searching/

  @Override
  public List<Cursor> getCursors(SearchRequest searchRequest) {
    boolean efficient = true;
    List<Cursor> cursorl = new LinkedList<Cursor>();
    String pattern = searchRequest.getPattern();
    String file = searchRequest.getFileName();
    for(int i = 0; i < lines.size(); i++)
    {
      String text = lines.get(i);
      int m = pattern.length();
      int n = text.length();
      int lps[] = new int[m];
      int j=0;
      computeLPSarray(pattern, m, lps);
      int k=0;
      while(k < n) {
        if(pattern.charAt(j) == text.charAt(k)) {
          j++;
          k++;
        }
        if(j == m) {
          Cursor cursor = new Cursor(i, k-j);
          cursorl.add(cursor);
          j = lps[j - 1];
        }
        else if (k < n && pattern.charAt(j) != text.charAt(k)) {
          if(j != 0) {
            j = lps[j - 1];
          }
          else {
            k = k + 1;
          }
        }
      }

      
    }
    return cursorl;
  }

  private void computeLPSarray(String pattern, int m, int[] lps) {
    int len = 0;
    int i = 1;
    lps[0] = 0;
    while(i < m) {
      if(pattern.charAt(i) == pattern.charAt(len)) {
        len++;
        lps[i] = len;
        i++;
      }
      else {
        if (len != 0) {
          len = lps[len - 1];

        }
        else {
          lps[i] = len;
          i++;
        }
      }
    }
  }
}



