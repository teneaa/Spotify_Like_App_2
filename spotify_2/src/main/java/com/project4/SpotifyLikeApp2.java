package com.project4;

//import static javax.sound.sampled.AudioSystem.*;

import java.io.*;
import java.util.*;
import javax.sound.sampled.*;
import org.json.simple.*;
import org.json.simple.parser.*;
// declares a class for the app
public class SpotifyLikeApp2 {
    
    // All global variables for the app
    String status;
    Long position;
    static String audioOps = "";
    static String theSong;
    static Clip audioClip;

    // Assumes there are no favorited songs before starting the program
    static Boolean favorites = false;

    // HashMaps used to get specific song data
    static HashMap<String,String> titleSearch = new HashMap<>();    
    static HashMap<String,String> findArtist = new HashMap<>();
    static HashMap<String,String> getGenre = new HashMap<>();
    static Map<String,Long> getSongYear = new HashMap<String,Long>();
    static Map<String,Integer> songFile = new HashMap<String,Integer>();
    static Map<Integer,String> songByIndex = new HashMap<Integer,String>();

    // getFavs acts a multiValue hashmap for traking true/ false for favorites var
    static Map<Boolean,ArrayList<String>> getFavs = new HashMap<Boolean,ArrayList<String>>();

/* ======================
All Private Functions
========================*/

// Func: forTrue()
// Desc: populates getFavs with (true, songs)
private static void forTrue() {
  getFavs.put(true, new ArrayList<String>());
}

// Func: forFalse()
// Desc: populates getFavs with (false, songs)
private static void forFalse() {
  getFavs.put(false, new ArrayList<String>());
}
// Func: showAudioMenu()
// Desc: Displays options within the play menu
private static void showAudioMenu() {
  System.out.println("************************************************************************************************");
  System.out.println("*----------------------------------------------------------------------------------------------*");
  System.out.println("*|||···   L[I]ke / [D]islike  ··· [P]lay  ···   p[A]use   ···   [S]top   ···   e[X]it ······|||*");
  System.out.println("*----------------------------------------------------------------------------------------------*");
  System.out.println("************************************************************************************************");
}

// Func: handlePlayMenu()
// Desc: Handles options for manipulating audio
private static void handlePlayMenu(JSONArray library, String st) {

  switch (st) {
    
    case "d":
    System.out.println("\n------------------------------> YOU DISLIKED THIS SONG <-------------------------------\n");
    System.out.println("======================  Romoved from your favorites list  ======================");
    audioClip.stop();
    getFavs.remove(favorites);
    System.out.println(getFavs);
    break;

    case "i":
      System.out.println("\n------------------------------> YOU LIKED THIS SONG! <-------------------------------\n");
      System.out.println("======================  Added to your favorites list  ======================");
      isFavorite(library);
      // Excluded break statement so that liked song will play immediately after liking the song.

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
        //Place holder for pause method
        audioClip.close();
        //Insert pause method here
      }
        
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
    st = st.toLowerCase();

    // let user handle audio
    handlePlayMenu(library, st);
  }
}

    /* =====================
    All Public Functions
    ========================*/

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
  
          //Place song and index number into songFile HashMap
          songFile.put((song),(songNum));

          //Place index number and song into songByFile HashMap
          songByIndex.put((songNum),(song));
        }
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
  public static JSONArray readAudioLibrary() {

    final String jsonFileName = "audio-library1.json";
    final String filePath = basePath + "/" + jsonFileName;
  
    // read the audio library of music
    JSONArray jsonData = readJSONArrayFile(filePath);
  
    System.out.println("\nReading the file " + filePath);
  
    return jsonData;  
  }
  
  // Func: play() 
  // Desc: plays an audio file
  private static void play(JSONArray library) {
  
    // open the audio file
    // get the filePath and open a audio file
    final Integer songIndex = songFile.get(theSong);
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

// Func: nowPlayingInfo()
// Desc: Shows all song information for the song that is currently playing
public static void nowPlayingInfo(JSONArray library) {
  for (Integer i = 0; i < library.size(); i++) {
    if (i == songFile.get(theSong)) {
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
  
  // Scanner and variable for playmenu:
  Scanner handleAudio = new Scanner(System.in);
  playMenu(audioOps,library, handleAudio);

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

    // User input prompt for indicating which song from the list should be played
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
    playMenu(audioOps, library, handleAudio);
}

// Func: isFavorite()
// Desc: handles likes and dislike feature 
public static void isFavorite(JSONArray library) {
  // populate getFavs for all songs
  for (Integer i = 0; i < library.size(); i++) {
    if (songFile.get(theSong) == i) {
      favorites = true;
      getFavs.get(true).add(songByIndex.get(i));
      System.out.println(getFavs.get(true));
    }
  }
  System.out.println("Favs after for loop:   " + getFavs.get(true));
}

// Func: showFavs()
// Desc: displays all favorite songs and gives user option to play a song from the list
public static void showFavs() {

  // Use for loop to display json file data
  if ((getFavs.get(true)).size() == 0 && (getFavs).get(false).size() == 0) {
    System.out.println("\nYou have not added any songs to this section yet.\n");
    System.out.println("Try searching for songs or picking a song from\n");
    System.out.println("the library catalog to add as favorites.\n");

  } else if (getFavs.get(true).size() > 0) {

    System.out.println("\n|============================================|\n");
    System.out.println("|| ············  FAVORITES!  ···············||\n");

    for (Integer i = 0; i < getFavs.get(true).size(); i++) {
      String audio = getFavs.get(true).get(i);
      System.out.print("\n               " +  audio + "                    \n");
    }
    System.out.println("\n  ···········································  ");
    System.out.println("|=============================================|\n");
  }
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
    
    case "!":
      //Scanner faveIn = new Scanner(System.in);
      //isFavorite(faveIn);

    case "f":
      
      // Show all favorites similar to library display
      showFavs();
      break;
    
    case "q":
      System.out.println("\n-->Quit<--\n");
      System.out.println("===============================================================================");
      break;
      
    default: 
      break;
    }
  }

  // Func: menu()
  // Desc: Displays menu for the app; different layout when home is selected
  public static void menu(JSONArray library) {
    nowPlayingInfo(library);
    System.out.println("\n|===================================|\n");
    System.out.println("||···· Kinda · Like · Spotify · By · Tenea ····||\n");
    System.out.println("|===================================|");
    System.out.println("|       [H]ome                      |");
    System.out.println("|-----------------------------------|");
    System.out.println("|       [S]earch by title           |");
    System.out.println("|-----------------------------------|");
    System.out.println("|       [L]ibrary                   |");
    System.out.println("|-----------------------------------|");
    System.out.println("|       [F]avorites                 |");
    System.out.println("|-----------------------------------|");
    System.out.println("|       [Q]uit                      |");
    System.out.println("|-----------------------------------|\n");
    System.out.print("       Enter q to Quit:              ");
  }

  private static String basePath =
  "/Users/teneaallen/Desktop/Fall 2022 /Java Programming/Assignments/Week_12/Spotify_Project/Spotify_Like_App_2/spotify_2/spotify_app_files";

  // "main" makes this class a java app that can be executed
  public static void main(final String[] args) {

    // Creates new arrays for when favorites == true and favorites == false
    forTrue();
    forFalse();

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
}