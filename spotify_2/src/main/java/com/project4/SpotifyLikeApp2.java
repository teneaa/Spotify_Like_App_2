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
  String status;
  Long position;
  static Clip audioClip;

  static HashMap<String,String> titleSearch = new HashMap<>();
  static HashMap<String,String> findArtist = new HashMap<>();
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
        Integer songNum = i;

        //Place song titles and file names titleSearch HashMap
        titleSearch.put((song),(file));

        //Place song and artist into findArtst HashMap
        findArtist.put((song),(artist));

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
  final String jsonFileName = "audio-library.json";
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
  final String filePath = basePath + "/wav/" + filename;
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

//handles the user input for the app

public static void handleMenu(String userInput, JSONArray library) {
  switch (userInput) {
    case "h":
    System.out.println("\n-->Home<--\n");
    System.out.println("===============================================================================");
      break;
    case "s":
      System.out.println("\n-->Search by title<--\n");
      System.out.println("===============================================================================");
      System.out.println("\nEnter the name of a song: \n");

      // create a scanner for title input
      Scanner input = new Scanner(System.in);

      // get title of a song
      String title = input.nextLine();

      // Assign theSong to the title that user input
      theSong = title;

      // Find the song info by title and prompt user to play the song
      System.out.println("\nThe file for that song is: " + titleSearch.get(title) + "\n");
      System.out.println("Type 'p' to play this song: ");

      // Provide user with more info about the main menu
      System.out.println("\nType 'l' to see where this song is located in the songs library catalog\n");
      System.out.println("\nType 'x' to stop/ pause the current song at any time.\n");
      break;

    case "l":
      System.out.println("\n-->Library<--\n");
      System.out.println("===============================================================================");

      // Use for loop to display json file data
      for (Integer i = 0; i < library.size(); i++) {
        JSONObject songs = (JSONObject) library.get(i);
        String song_name = (String) songs.get("name");
        System.out.print(i + 1 + "| ");
        System.out.print("Song: " +  song_name + "| Artist: " + findArtist.get(song_name));
        System.out.println("| File: " + titleSearch.get(song_name) + "\n");
      }

      // User input prompt for indicating which
      // song from the list should be played
      System.out.println("===============================================================================");
      System.out.println("Which song from the list would you like to listen to?"); 
      System.out.println("\nex: type '1' for Cement Lunch \n");

      // Create Scanner for song input
      Scanner numIn = new Scanner(System.in);
      Integer pickNum = numIn.nextInt();
      Integer j = 0;
      while (j != pickNum) {
        j++;
      }

      // Confirms the user's song choice
      if (j == pickNum) {
        JSONObject song = (JSONObject) library.get(j - 1);
        String songPick = (String) song.get("name");
        System.out.println("\nYou chose: \n\n" + songPick + ".\n\nIf you would like to play this song, type 'p'.\n");
        System.out.println("\nType 'x' to stop/ pause the current song at any time.\n");
        theSong = songPick;
      }
      break;

    case "p":
      
      System.out.println("\n-->Play<--\n");
      System.out.println("===============================================================================");
      play(library);
      break;

    case "a":

      // Stops current audio clip from playing
      // but does not quit the program
      audioClip.close();

    case "q":
      System.out.println("\n-->Quit<--\n");
      System.out.println("===============================================================================");
      break;
    default: 
      break;
    }
  }

  private static String basePath =
    "/Users/teneaallen/Desktop/Fall 2022 /Java Programming/Assignments/Week_10/Spotify_Project/Spotify_Like_App_1/spotify_1/spotify-example-code2";

  // "main" makes this class a java app that can be executed
  public static void main(final String[] args) {
    // reading audio library from json file
    JSONArray library = readAudioLibrary();

    // create a scanner for user input
    Scanner input = new Scanner(System.in);

    String userInput = "";
    while (!userInput.equals("q")) {

      menu();

      // get input
      userInput = input.nextLine();

      // handle upper and lower case entries
      userInput = userInput.toLowerCase();

      // do something
      handleMenu(userInput, library);
    }

    // close the scanner
    input.close();
  }

  /*
   * displays the menu for the app
   * displays different layout for when home is selectedS
   */
  public static void menu() {
    System.out.println("\n---- SpotifyLikeApp ----\n");
    System.out.println("[H]ome");
    System.out.println("[S]earch by title");
    System.out.println("[L]ibrary");
    System.out.println("[P]lay");
    System.out.println("p[A]use");
    System.out.println("[Q]uit");

    System.out.println("");
    System.out.print("Enter q to Quit:  ");
  }
    
}
