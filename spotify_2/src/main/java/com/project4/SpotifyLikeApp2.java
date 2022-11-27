package com.project4;

//import static javax.sound.sampled.AudioSystem.*;

import java.io.*;
import java.util.*;
import javax.sound.sampled.*;
import org.json.simple.*;
import org.json.simple.parser.*;
// declares a class for the app
public class SpotifyLikeApp2 {
    
    // global variables for the app
    static String status = "";
    Long position;
    static Clip audioClip;

    static HashMap<String,String> titleSearch = new HashMap<>();
    static HashMap<String,String> findArtist = new HashMap<>();
    static HashMap<String,String> getGenre = new HashMap<>();
    static Map<String,Long> getSongYear = new HashMap<String,Long>();
    static Map<String,Integer> playSongFile = new HashMap<String,Integer>();
    static String theSong;
  
    // Func: readJSONFile
    // Desc: Reads a json file storing an array and returns an object
    // that can be iterated over

    public static JSONArray readJSONArrayFile(String fileName) {
    // JSON parser object to parse read file
    JSONParser jsonParser = new JSONParser();

    JSONArray dataArray = null;

    try (FileReader reader = new FileReader(fileName)) {
      // Read JSON file
      Object obj = jsonParser.parse(reader);

      dataArray = (JSONArray) obj;

      // loop through all items in json file to assing objects
      // to string 
      // Create JSON object

      JSONObject Items;
      for (Integer i = 0; i < dataArray.size(); i++) {

        //Parses object pulls out the song tile and filename
        Items = (JSONObject) dataArray.get(i);

        String song = (String) Items.get("name");
        String file = (String) Items.get("filename");
        String artist = (String) Items.get("artist");
        // Objects that still need to be placed into hashmaps:
        String genre = (String) Items.get("genre");
        Long releaseYear = (Long) Items.get("year");

        Integer songNum = i;

        //Place song titles and file names titleSearch HashMap
        titleSearch.put((song),(file));

        //Place song and artist into findArtst HashMap
        findArtist.put((song),(artist));

        //Place song and genre into getGenre HashMap
        getGenre.put((song),(genre));

        //Place song and release year into getSongYear HashMap
        getSongYear.put((song),(releaseYear));

        //Place song and index number into playSongFile HashMap
        playSongFile.put((song),(songNum));
      }
      //System.out.println(dataArray);

    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    } catch (ParseException e) {
      e.printStackTrace();
    }

    return dataArray;
}

// Func: readAudioLibrary()
// Desc: Gets information about the audio files

// read the audio library of music
public static JSONArray readAudioLibrary() {
  final String jsonFileName = "audio-library1.json";
  final String filePath = basePath + "/" + jsonFileName;

  JSONArray jsonData = readJSONArrayFile(filePath);

  System.out.println("\nReading the file " + filePath);

  return jsonData;  
}

// Func: play() 
// Desc: plays an audio file

public static void play(JSONArray library) {

  // open the audio file
  // get the filePath and open a audio file
  final Integer songIndex = playSongFile.get(theSong);
  JSONObject obj = (JSONObject) library.get(songIndex);
  final String filename = (String) obj.get("filename");
  final String filePath = basePath + "/wav1/" + filename;
  final File file = new File(filePath);

  // stop the current song from playing, before playing the next one
  if (audioClip != null) {
    audioClip.close();
  }

  try {
    // create clip
    audioClip = AudioSystem.getClip();

    // get input stream
    final AudioInputStream in = AudioSystem.getAudioInputStream(file);

    audioClip.open(in);
    audioClip.setMicrosecondPosition(0);
    audioClip.loop(Clip.LOOP_CONTINUOUSLY);
  } catch (Exception e) {
    e.printStackTrace();
  }
}

// Func: showAudioMenu()
// Desc: Displays options within the play menu
private static void showAudioMenu() {
  System.out.println("*************************************************************************");
  System.out.println("*-----------------------------------------------------------------------*");
  System.out.println("*|||······ [P]lay  ···   p[A]use   ···   [S]top   ···   e[X]it ······|||*");
  System.out.println("*-----------------------------------------------------------------------*");
  System.out.println("*************************************************************************");
}

// Func: handlePlayMenu()
// Desc: Handles options for manipulating audio
private static void handlePlayMenu(JSONArray library, String st) {

  switch (st) {
    
    case "p":
      System.out.println("\n------------------------------> NOW PLAYING <-------------------------------\n");
      System.out.println("================================================================================");
      play(library);
      break;
    
    case "a":
      if (audioClip == null) {
        System.out.println("There is no audio currently playing.\n\nPress 'p' to play a song first");
        System.out.println("or press 'x' to exit.\n\n");
      } else {
        System.out.println("\n--------------------------------> PAUSED <----------------------------------\n");
        System.out.println("================================================================================");
      }
        //Place holder for pause method
        audioClip.close();
        //Insert pause method here
      break;

    case "s":
      
      // Handle error when user chooses "stopped", but no song is playing
      if (audioClip == null) {
        System.out.println("There is no audio currently playing.\n\nPress 'p' to play a song first");
        System.out.println("or press 'x' to exit.\n\n");
      } else {
        System.out.println("\n---------------------------------> STOPPED <----------------------------------\n");
        System.out.println("==================================================================================");
        audioClip.stop();
      }
      
      break;

    case "x":
      
      // Exits play menu without stopping song play
      System.out.println("\n---------------------------------> EXITED PLAY MENU <----------------------------------\n");
      System.out.println("==================================================================================");
   
    default: 
      break;
  }
}

// Func: playMenu()
// Desc: Handles options for manipulating audio and provides audio status info
private static void playMenu(String st, JSONArray library, Scanner sc) {

  // Display UI audio options menu
  showAudioMenu();

  while (!st.equals("x")) {

    // get user input
    st = sc.nextLine();

    // handle upper and lower case entries
    status = st.toLowerCase();

    // let user handle audio
    handlePlayMenu(library, status);
  }
}

// Func: nowPlayingInfo()
// Desc: Shows all song information for the song that is currently playing

public static void nowPlayingInfo(JSONArray library) {
  for (Integer i = 0; i < library.size(); i++) {
    if (i == playSongFile.get(theSong)) {
      JSONObject songs = (JSONObject) library.get(i);
      String song_name = (String) songs.get("name");
      System.out.print("Song: " +  song_name + "| Artist: " + findArtist.get(song_name));
      System.out.print(" | Year: " + getSongYear.get(song_name));
      System.out.print(" | Genre: " + getGenre.get(song_name));
      System.out.println("| File: " + titleSearch.get(song_name) + "\n");
    };
  }

}

// Func: handleCaseS()
// Desc: handles searching for songs by title

public static void handleCaseS(Scanner sc, JSONArray library) {
  System.out.println("\n-->Search by title<--\n");
  System.out.println("===============================================================================");
  System.out.println("\nEnter the name of a song: \n");

  // get title of a song
  String title = sc.nextLine();

  // Assign theSong to the title that user input
  theSong = title;

  // Find the song info by title and prompt user to play the song
  System.out.println("\nThe file for that song is: " + titleSearch.get(title) + "\n");
  
  // Scanner for playmenu:
  Scanner handleAudio = new Scanner(System.in);
  playMenu(status,library, handleAudio);

}
// Func: handleCaseL()
// Desc: handles selecting songs from library index

public static void handleCaseL(JSONArray library, Scanner sc) {

    System.out.println("\n-->Library<--\n");
    System.out.println("===============================================================================");

    // Use for loop to display json file data
    for (Integer i = 0; i < library.size(); i++) {
      JSONObject songs = (JSONObject) library.get(i);
      String song_name = (String) songs.get("name");
      System.out.print(i + 1 + "| ");
      System.out.print("Song: " +  song_name + "| Artist: " + findArtist.get(song_name));
      System.out.print(" | Year: " + getSongYear.get(song_name));
      System.out.print(" | Genre: " + getGenre.get(song_name));
      System.out.println("| File: " + titleSearch.get(song_name) + "\n");
    }

    // User input prompt for indicating which
    // song from the list should be played
    System.out.println("===============================================================================");
    System.out.println("Which song from the list would you like to listen to?"); 
    System.out.println("\nex: type '1' for Cement Lunch \n");

    Integer pickNum = sc.nextInt();

    // Locates song indexed in the library
    JSONObject song = (JSONObject) library.get(pickNum - 1);
    
    String songPick = (String) song.get("name");
    System.out.println("\nYou chose:\n================================ ");
    System.out.println("||     " + songPick + "      ||");
    System.out.println("================================\n");
    theSong = songPick;

    // Scanner for playmenu:
    Scanner handleAudio = new Scanner(System.in);
    playMenu(status, library, handleAudio);
}

// Func: handleMenu()
// Desc: handles the user input for the app

public static void handleMenu(String userInput, JSONArray library) {
  switch (userInput) {
    case "h":
      System.out.println("\n-->Home<--\n");
      System.out.println("===============================================================================");
      break;
    case "s":

      Scanner input = new Scanner(System.in);
      handleCaseS(input, library);
      break;

    case "l":
      
      Scanner numIn = new Scanner(System.in);  
      handleCaseL(library, numIn);
      break;

    /*case "f":
      Scanner faveIn = new Scanner(System.in);
      handleCaseF();  
      break;*/

    case "p":
      
      System.out.println("\n-->Play<--\n");
      System.out.println("===============================================================================");
      play(library);

      break;

    case "a":

      // Pauses currently playing audio
      audioClip.close();
      break;

    case "t": 
      // Stops current audio file and returns user to menu()
      audioClip.stop();
      theSong = null;
      break;
    case "q":
      System.out.println("\n-->Quit<--\n");
      System.out.println("===============================================================================");
      break;
      
    default: 
      break;
    }
  }

  private static String basePath =
    "/Users/teneaallen/Desktop/Fall 2022 /Java Programming/Assignments/Week_12/Spotify_Project/Spotify_Like_App_2/spotify_2/spotify_app_files";

  // "main" makes this class a java app that can be executed
  public static void main(final String[] args) {
    // reading audio library from json file
    JSONArray library = readAudioLibrary();

    // create a scanner for user input
    Scanner menuInput = new Scanner(System.in);

    String userInput = "";

    while (!userInput.equals("q")) {
        
        menu(library);
        
        // get user input
        userInput = menuInput.nextLine();

        // handle upper and lower case entries
        userInput = userInput.toLowerCase();

        // do something
        handleMenu(userInput, library);
    }

    // close the scanner
    menuInput.close();
  }

  /*
   * displays the menu for the app
   * displays different layout for when home is selectedS
   */
  public static void menu(JSONArray library) {
    nowPlayingInfo(library);
    System.out.println("\n---- SpotifyLikeApp ----\n");
    System.out.println("[H]ome");
    System.out.println("[S]earch by title");
    System.out.println("[L]ibrary");
    System.out.println("[P]lay");
    System.out.println("p[A]use");
    System.out.println("S[T]op");
    System.out.println("[F]avorites");
    System.out.println("[Q]uit");

    System.out.println("");
    System.out.print("Enter q to Quit:  ");
  }
}