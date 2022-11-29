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
    static String status;
    static Integer position;
    static String audioOps = "";
    static String theSong;
    static Clip audioClip;

    // Assumes there are no favorited songs before starting the program
    static Boolean isFavorite = false;

    // HashMaps used to get specific song data
    static HashMap<String,String> titleSearch = new HashMap<>();    
    static HashMap<String,String> findArtist = new HashMap<>();
    static HashMap<String,String> getGenre = new HashMap<>();
    static Map<String,Long> getSongYear = new HashMap<String,Long>();
    static Map<String,Integer> songFile = new HashMap<String,Integer>();
    static Map<Integer,String> songByIndex = new HashMap<Integer,String>();

    // getFavs acts a multiValue hashmap for traking true/ false for isFavorite var
    static Map<Boolean, ArrayList<String>> getFavs = new HashMap<Boolean, ArrayList<String>>();

    /*
     * ======================
     * All Private Functions
     * ========================
     */

    // Func: forTrue()
    // Desc: populates getFavs with (true, songs)
    private static void forTrue() {
      getFavs.put(true, new ArrayList<String>());
    }

    //Func: forFalse()
    //Desc: populates getFavs with (false, songs)
    private static void forFalse() {
      getFavs.put(false, new ArrayList<String>());
    }

    // Func: pickMenuOrDisplay()
    // Desc: Decides which menu to display when program begins
    static private void pickMenuOrDisplay(JSONArray lib, String st) {
      // condition for displaying home menu vs main menu
      if (st.equals("h")) {
        homeMenu(lib);
      } else if (st.equals("f")) {
        showFavs();
      } else {
        menu(lib);
      }
    }

    // Func: goodBye()
    // Desc: Shows thank you message when user quits the program
    private static void goodBye() {

      System.out.println("\n********************************************************************************");
      System.out.println("* · · · · · · · · · · · ·  THANK YOU FOR LISTENING WITH · · · · · · · · · ·  · *");
      System.out.println("* · · · · · · · · · · · · ·   KINDA · LIKE · SPOTIFY   · · · · · · · · · · · · *");
      System.out.println("********************************************************************************\n");
    }

    // Func: userPlaysSong()
    // Desc: Display announcement about song status 
    private static void userPlaysSong(JSONArray lib) {

          System.out.println("\n==================================================================================");
          System.out.println("|| · · · · · · · · · · · · · · · NOW PLAYING  · · · · · · · · · · · · · · · · · · ||");
          System.out.println("                               " + theSong + "                                        ");
          System.out.println("==================================================================================\n");
          play(lib);
    }

    // Func: rewind()
  // Desc: Rewinds current audio file back 5 seconds
  private static void rewind(JSONArray library) {
    position = audioClip.getFramePosition();
    Integer rrAmount = 5;
    audioClip.setFramePosition(position - rrAmount);
  }

  // Func: fastforward()
  // Desc: Fastforwards current audio file forward 5 seconds
  private static void fastforward(JSONArray library) {
    position = audioClip.getFramePosition();
    Integer ffAmount = 5;
    audioClip.setFramePosition(position + ffAmount);
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
      audioClip.setFramePosition(0);
      audioClip.loop(Clip.LOOP_CONTINUOUSLY);
    } catch (Exception e) {
      e.printStackTrace();
    }

    // Determine status of current song
    status = "playing";
  }

    // Func: userPausedSong()
    // Desc: Display announcement about song status
    private static void userPausedSong() {

      if (audioClip == null) {
        System.out.println("There is no audio currently playing.\n\nPress 'p' to play a song first");
        System.out.println("or press 'x' to exit.\n\n");
      } else {
        System.out.println("\n================================================================================");
        System.out.println("|| · · · · · · · · · · · · · · · PAUSED · · · · · · · · · · · · · · · · · · · ||");
        System.out.println("                               " + theSong + "                                     ");
        System.out.println("================================================================================\n");
        
        // Uses stop() to pause clip at current frame
        audioClip.stop();
        
      }
    }

    // Func: resumePausedSong()
    // Desc: plays song from paused state
    private static void resumePausedSong() {
      
      if (audioClip == null) {
        System.out.println("There is no audio currently playing.\n\nPress 'p' to play a song first");
        System.out.println("or press 'x' to exit.\n\n");
      } else {
        System.out.println("\n================================================================================");
        System.out.println("|| · · · · · · · · · · · · · · · · RESUMING · · · · · · · · · · · · · · · · · · ||");
        System.out.println("                               " + theSong + "                                     ");
        System.out.println("================================================================================\n");
      }
      // Uses start() to resume song in paused state
      audioClip.start();
    }

    // Func: userStoppedSong()
    // Desc: Display announcent about song status
    private static void userStoppedSong() {

      // Handle error when user chooses "stopped", but no song is playing
      if (audioClip == null) {
        System.out.println("There is no audio currently playing.\n\nPress 'p' to play a song first");
        System.out.println("or press 'x' to exit.\n\n");
      } else {
        System.out.println("\n================================================================================");
        System.out.println("||· · · · · · · · · · · · · · · · STOPPED · · · · · · · · · · · · · · · · · · ||");
        System.out.println("                                 " + theSong + "                                   ");
        System.out.println("================================================================================\n");
        audioClip.close();
      }
    }

    // Func: likedSong()
    // Desc: Displays announcement about liked song
    private static void likedSong(JSONArray lib) {

      System.out.println("=============================================================================");
      System.out.println("· · · · · · · · · · · · · YOU LIKED THIS SONG! · · · · · · · · · · · · · · · ");
      System.out.println("\n· · · · · · · " + theSong + " has been added to your Favorites.· · · · · · ·\n");
      System.out.println("=============================================================================");
      isFav(lib);
    }

    // Func: dislikedSong()
    // Desc: Displays announcement about disliked song
    //       Removes song from favorites list.
    private static void dislikedSong(JSONArray lib) {
      System.out.println("================================================================================");
      System.out.println("\n· · · · · · · · · · · ·  YOU DISLIKED THIS SONG · · · · · · · · · · · · · · ·\n");
      System.out.println("\n· · · · · · · " + theSong + " has been removed your Favorites.· · · · · · · · ·\n");
      System.out.println("================================================================================");
      audioClip.stop();

      //Uses for loop from isFav() to remove song from favorites list
      for (Integer i = 0; i < lib.size(); i++) {

        if (songFile.get(theSong) == i) {
          isFavorite = false;
          
          // Remove matching song from arraylist inside hashmap
          getFavs.get(true).remove(songByIndex.get(i));
        }
      }
    }

    // Func: exitsPlayMenu()
    // Desc: Display announcement when leaving play menu
    private static void exitsPlayMenu() {
      // Exits play menu without stopping song play
      System.out.println("\n================================================================================");
      System.out.println("||· · · · · · · · · · · · ·  EXITED PLAY MENU · · · · · · · · · · · · · · · · ||");
      System.out.println("||· · · · · · · · · · · · · · · · · · · · · · · · · · · · · · · · · · · · · · ||");
      System.out.println("================================================================================\n");
    }

    // Func: showAudioMenu()
    // Desc: Displays options within the play menu
    private static void showAudioMenu() {
      System.out.println("\n************************************************************************************************");
      System.out.println("*                               A U D I O  · C O N T R O L S                                   *");
      System.out.println("*----------------------------------------------------------------------------------------------*");
      System.out.println("*|||······    L[I]KE  ··· [P]LAY  ···   P[A]USE   ···   RES[U]ME ···  [S]TOP          ······|||*");
      System.out.println("*----------------------------------------------------------------------------------------------*");
      System.out.println("*----------------------------------------------------------------------------------------------*");
      System.out.println("*|||······    [D]ISLIKE  ··· [F]ASTFORWARD  ···   [R]EWIND   ···   E[X]IT             ······|||*");
      System.out.println("*----------------------------------------------------------------------------------------------*");
      System.out.println("************************************************************************************************\n");
      System.out.print("\n                            Enter 'X' to Exit:              ");
    }

    // Func: handlePlayMenu()
    // Desc: Handles options for manipulating audio
    private static void handlePlayMenu(JSONArray library, String st) {

      switch (st) {

        case "d":
          
          dislikedSong(library);
          break;

        case "i":

          likedSong(library);
          break;

        case "p":

          userPlaysSong(library);
          break;

        case "a":

          userPausedSong();
          break;

        case "u":

          resumePausedSong();
          break;

        case "r":
          
          rewind(library);
          break;

        case "f":

        fastforward(library);
        break;

        case "s":

          userStoppedSong();
          break;

        case "x":

        // Redirects user back to the home menu
        exitsPlayMenu();

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

// Func: currentTrack()
// Desc: Shows all song information for the song that is currently playing
public static void currentTrack(JSONArray library) {
  for (Integer i = 0; i < library.size(); i++) {
    if (i == songFile.get(theSong)) {
      JSONObject songs = (JSONObject) library.get(i);
      String song_name = (String) songs.get("name");
      System.out.print("\n\nCurrent Track:  ");
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
  System.out.println("================================================================================");
  System.out.println("|| · · · · · · · · · · · · · ·  SEARCH · BY · TITLE · · · · · · · · · · · · · ||");
  System.out.println("================================================================================");
  System.out.println("\nEnter the name of a song: \n");

  // get title of a song
  String title = sc.nextLine();

  // Assign theSong to the title that user input
  theSong = title;

  // Finds the song info by title and displays song file info
  System.out.println("\nThe file for that song is: " + titleSearch.get(title) + "\n");
  
  // Scanner and variable for playmenu:
  // Gives users options for manipulating audio
  Scanner handleAudio = new Scanner(System.in);
  playMenu(audioOps,library, handleAudio);

}

// Func: handleCaseL()
// Desc: handles selecting songs from library index
public static void handleCaseL(JSONArray library, Scanner sc) {

  System.out.println("\n================================================================================");
  System.out.println("|| · · · · · · · · · · · · · ·  MUSIC · LIBRARY · · · · · · · · · · · · · · · ||");
  System.out.println("================================================================================\n");

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
    
    // Outputs chosen song for user to view
    String songPick = (String) song.get("name");
    System.out.println("You chose:  =================================");
    System.out.println("              ||     " + songPick + "         ||");
    System.out.println("            =================================");
    theSong = songPick;

    // Scanner for playmenu
    // Gives users options for manipulating audio
    Scanner handleAudio = new Scanner(System.in);
    playMenu(audioOps, library, handleAudio);
}

// Func: isFav()
// Desc: handles likes and dislike feature 
public static void isFav(JSONArray library) {
  // populate getFavs for all songs
  for (Integer i = 0; i < library.size(); i++) {

    if (songFile.get(theSong) == i) {
      isFavorite = true;
      
      // Add matching song to arraylist inside hashmap
      getFavs.get(true).add(songByIndex.get(i));
    }
  }
}

// Func: showFavs()
// Desc: displays all favorite songs and gives user option to play a song from the list
public static void showFavs() {

  // Use for loop to display json file data
  if ((getFavs.get(true)).size() == 0 && (getFavs).get(false).size() == 0) {
    System.out.println("\n||============================================||");
    System.out.println("|| ············  FAVORTIES!  ·················||");
    System.out.println("||············································||");
    System.out.println("||============================================||");
    System.out.println("\nYou have not added any songs to this section yet.\n");
    System.out.println("Try searching for songs or picking a song from\n");
    System.out.println("the library catalog to add as Favorites.\n\n");

  } else if (getFavs.get(true).size() > 0) {

    System.out.println("\n||============================================||");
    System.out.println("|| ············  FAVORTIES!  ·················||");

    for (Integer i = 0; i < getFavs.get(true).size(); i++) {
      String audio = getFavs.get(true).get(i);
      System.out.print("\n               " +  audio + "                    \n");
    }
    System.out.println("||············································||");
    System.out.println("||============================================||\n\n");
    System.out.println("      Enter 'I' to [LIKE] the current track     \n");
    System.out.println("      Enter 'D' to [DISLIKE] the current track   \n");
  }
}

// Func: homeMenu()
// Desc: Displays home menu for the app
public static void homeMenu(JSONArray library) {

  //Condition for showing current song
  if (audioClip != null) {
    currentTrack(library);
  }

  System.out.println("\n*****************************************************************************");
  System.out.println("*                       ······  H · O · M · E ······                        *");
  System.out.println("*---------------------------------------------------------------------------*"); 
  System.out.println("*|||··········                [F]AVORITES                      ··········|||*");
  System.out.println("*---------------------------------------------------------------------------*");
  System.out.println("*---------------------------------------------------------------------------*"); 
  System.out.println("*|||··········                [L]IBRARY                        ··········|||*");
  System.out.println("*---------------------------------------------------------------------------*");
  System.out.println("*---------------------------------------------------------------------------*"); 
  System.out.println("*|||··········                [S]EARCH · BY · TITLE            ··········|||*");
  System.out.println("*---------------------------------------------------------------------------*");
  System.out.println("*---------------------------------------------------------------------------*"); 
  System.out.println("*|||··········                E[X]IT                           ··········|||*");
  System.out.println("*---------------------------------------------------------------------------*");
  System.out.println("*****************************************************************************\n");
  System.out.print("\n                            Enter 'X' to Exit:              ");
}

// Func: handleMenu()
// Desc: handles the user input for the app
public static void handleMenu(String userInput, JSONArray library) {
  switch (userInput) {
    case "h":
      // Displays home menu based on pickMenuOrDisplay()
      break;

    case "s":

      Scanner input = new Scanner(System.in);
      handleCaseS(input, library);
      break;

    case "l":
      
      Scanner numIn = new Scanner(System.in);  
      handleCaseL(library, numIn);
      break;

    case "i":

      likedSong(library);
      break;

    case "d":
      dislikedSong(library);
      break;

    case "f":
      
      // Shows favorites or error message based on menue or display
      break;
    
    case "g":
      goodBye();
      break;
      
    default: 
      break;
    }
  }

  // Func: menu()
  // Desc: Displays menu for the app; different layout when home is selected
  public static void menu(JSONArray library) {

    //Condition for showing most recently played song
    if (audioClip != null && status == "playing") {
      currentTrack(library);
    }
    
    System.out.println("\n\n*****************************************************************************");
    System.out.println("*                    ·······  KINDA · LIKE · SPOTIRY ·······                *");
    System.out.println("*---------------------------------------------------------------------------*"); 
    System.out.println("*|||··········                [H]OME                           ··········|||*");
    System.out.println("*---------------------------------------------------------------------------*");
    System.out.println("*---------------------------------------------------------------------------*"); 
    System.out.println("*|||··········                [S]EARCH BY TITLE                ··········|||*");
    System.out.println("*---------------------------------------------------------------------------*");
    System.out.println("*---------------------------------------------------------------------------*"); 
    System.out.println("*|||··········                [L]IBRARY                        ··········|||*");
    System.out.println("*---------------------------------------------------------------------------*");
    System.out.println("*---------------------------------------------------------------------------*"); 
    System.out.println("*|||··········                [F]AVORITES                      ··········|||*");
    System.out.println("*---------------------------------------------------------------------------*");
    System.out.println("*---------------------------------------------------------------------------*"); 
    System.out.println("*|||··········                [G]OOD BYE                       ··········|||*");
    System.out.println("*---------------------------------------------------------------------------*");
    System.out.println("*****************************************************************************\n");
    System.out.print("\n                            Enter 'G' to say Goodbye:              ");
  }

  private static String basePath =
  "/Users/teneaallen/Desktop/Fall 2022 /Java Programming/Assignments/Week_12/Spotify_Project/Spotify_Like_App_2/spotify_2/spotify_app_files";

  // "main" makes this class a java app that can be executed
  public static void main(final String[] args) {

    // Creates new arrays for when isFavorite == true and when isFavorite == false
    forTrue();
    forFalse();

    // reading audio library from json file
    JSONArray library = readAudioLibrary();

    // create a scanner for user input
    Scanner menuInput = new Scanner(System.in);

    String userInput = "";

    while (!userInput.equals("g")) {

      // get menu()
      pickMenuOrDisplay(library, userInput);
        
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