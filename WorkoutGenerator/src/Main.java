import java.net.URL;
import java.net.URLConnection;
import java.util.Collections;
import java.util.LinkedList;
import java.io.*;

public class Main {
	public static void main(String[] args) throws IOException {
		File abList = new File("src/abList.txt");
		File bicepList =  new File("src/bicepList.txt");
		File calfList  = new File("src/calfList.txt");
		File chestList = new File("src/chestList.txt");
		File hamstringList = new File("src/hamstringList.txt");
		File latList = new File("src/latList.txt");
		File quadList = new File("src/quadList.txt");
		File shoulderList = new File("src/shoulderList.txt");
		File tricepList = new File("src/tricepList.txt");
		
		  pullExercises(abList, bicepList, calfList, chestList, hamstringList, latList, quadList, shoulderList, tricepList);
		  boolean isWorkout = false;		  
		  while (!isWorkout){
			  isWorkout = generateWorkout(abList, bicepList, calfList, chestList, hamstringList, latList, quadList, shoulderList, tricepList);			
			  if (!isWorkout){
				  System.out.println("Suboptimal plan generated. Attempting to generate a better workout...");
				  System.out.println();
			  }
		  }
	}
	
	
	public static class Exercise {
		String name = "";
		String type = "";
		String equipment = "";
		URL url;
		String primaryMuscle = "";
		String[] secondaryMuscles = {"", "", "", "", "", "", "", "", "", "", "", ""};
		public Exercise(String exerciseName, String exerciseLink) throws IOException{
			name = exerciseName;
			URL url = new URL(exerciseLink);
			  URLConnection connection = url.openConnection();
			  BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			  
			  //Scan each line for muscle groups
			  String strLine = "";
			  boolean isPrimaryMuscle = false;
			  boolean isSecondaryMuscle = false;
			  boolean isEquipment = false;
			  int secondaryIndex = 0;
			  while ((strLine = in.readLine()) != null){
				   //Pulls exercise type into String type
				   if (strLine.contains("               <span class=\"row\">Type: ")){
					   int typeIndex = strLine.indexOf(">");
					   typeIndex = strLine.indexOf(">", typeIndex + 1) + 1; 
					   int endIndex = strLine.indexOf("</a>");
					   type = strLine.substring(typeIndex, endIndex);
				   }   
				   
				   //Pulls equipment into String equipment
				   if (strLine.contains("                    <span class=\"row\">Equipment:")){
					   isEquipment = true;
				   }   
				   else if (isEquipment){
					   int endIndex = 0;
					   int startIndex = 0;
					   while (strLine.indexOf("</a>",endIndex) != -1){
						   endIndex = strLine.indexOf("</a>", endIndex);
						   startIndex = strLine.indexOf("'>", startIndex) + 2;
						   String equip = strLine.substring(startIndex, endIndex);
						   equipment = equip;
						   startIndex++;
						   endIndex++;
					   }
					   isEquipment = false;
				   }
				   //Pulls primary muscle into String primaryMuscle
				   if (strLine.contains("                	<span class=\"row\">Main Muscle Worked:")){
					   isPrimaryMuscle = true;
				   }   
				   else if (isPrimaryMuscle){
					   int endIndex = 0;
					   int startIndex = 0;
					   while (strLine.indexOf("</a>",endIndex) != -1){
						   endIndex = strLine.indexOf("</a>");
						   startIndex = strLine.indexOf("'>", startIndex) + 2;
						   primaryMuscle = strLine.substring(startIndex, endIndex);
						   startIndex++;
						   endIndex++;
					   }
					   isPrimaryMuscle = false;
				   }
				   //Pulls secondary muscle groups into String[] secondaryMuscles
				   if (strLine.contains("                    <span class=\"row\">Other Muscles:")){
					   isSecondaryMuscle = true;
				   } else if (isSecondaryMuscle){
					   int endIndex = 0;
					   int startIndex = 0;
					   while (strLine.indexOf("</a>",endIndex) != -1){
						   endIndex = strLine.indexOf("</a>", endIndex);
						   startIndex = strLine.indexOf("'>", startIndex) + 2;
						   String secondaryMuscle = strLine.substring(startIndex, endIndex);
						   secondaryMuscles[secondaryIndex] = secondaryMuscle;
						   startIndex++;
						   endIndex++;
						   secondaryIndex++;
					   }
					   isSecondaryMuscle = false;
				   }
			 }
		}
	}
	
	//Cycles through webpages to store exercise names and links in associated muscle group files
	public static void pullExercises(File abList, File bicepList, File calfList, File chestList, File hamstringList, 
			File latList, File quadList, File shoulderList, File tricepList) throws IOException{
		
		  File[] files = {abList, bicepList, calfList, chestList, hamstringList, latList, quadList, shoulderList, tricepList};
		  String[] muscleTypes = {"abdominals", "biceps", "calves", "chest", "hamstrings", "lats", "quadriceps", "shoulders", "triceps"};
		  int typeIndex = muscleTypes.length - 1;
		  
		//Cycle through all sources
		while(typeIndex >= 0){
		  URL url = new URL("http://www.bodybuilding.com/exercises/list/muscle/selected/" + muscleTypes[typeIndex]);
		  URLConnection connection = url.openConnection();
		  BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
		  String strLine = "";
		  PrintWriter writer = new PrintWriter(files[typeIndex]);
		  boolean isExercise = false;
		  
		  //Scan every line in the source
		  while ((strLine = in.readLine()) != null){
		   //Write exercise name and link to file
		   if (isExercise == true && strLine.contains("                    <h3><a href='http://www.bodybuilding.com")){
			   int endIndex = strLine.indexOf("'>");
			   int startIndex = strLine.indexOf("http");
			   String exerciseLink = strLine.substring(startIndex, endIndex);
			   String exerciseName = strLine.substring(endIndex + 3, strLine.indexOf("</a>") - 1);
			   writer.println(exerciseName);
			   writer.println(exerciseLink);			   
			   isExercise = false;
		   }
		   if(strLine.indexOf("exerciseName") != -1){
			   isExercise = true;
		   }
		  }
		  writer.close();
		  typeIndex--;
		}
}	
	//Generates workout routine and returns whether full routine generated. Prints routine if full routine generated.
	public static boolean generateWorkout(File abList, File bicepList, File calfList, File chestList, File hamstringList, File latList, File quadList, File shoulderList, File tricepList) throws IOException{
		Exercise[] chestPrimary = new Exercise[5];
		Exercise[] latPrimary = new Exercise[5];
		Exercise[] hamstringPrimary = new Exercise[5];
		Exercise[] quadPrimary = new Exercise[5];
		Exercise[] calfPrimary = new Exercise[5];
		Exercise[] shoulderPrimary = new Exercise[5];
		Exercise[] tricepPrimary = new Exercise[5];
		Exercise[] bicepPrimary = new Exercise[5];
		Exercise[] absPrimary = new Exercise[5];
				
	    LinkedList<Integer> numLines = new LinkedList<Integer>();
	    LinkedList<File> fileList = new LinkedList<File>();
	    fileList.addLast(abList);
	    fileList.addLast(bicepList);
	    fileList.addLast(calfList);
	    fileList.addLast(chestList);
	    fileList.addLast(hamstringList);
	    fileList.addLast(latList);
	    fileList.addLast(quadList);
	    fileList.addLast(shoulderList);
	    fileList.addLast(tricepList);
		Collections.shuffle(fileList);

	    int filesIndex = fileList.size();
		int lineCount;
		//Put number of lines in each exercise file into numLines
		while (filesIndex > 0){
			lineCount = 0;
			File listPoll = fileList.pollFirst();
			BufferedReader listReader = new BufferedReader(new FileReader(listPoll));
		    while (listReader.readLine() != null){
		    	lineCount++;
		    }		    
			listReader.close();
			fileList.addLast(listPoll);
			numLines.addLast(lineCount);
			filesIndex--;
		}
		
		int numChestPrimary = 0;
		int numLatPrimary = 0;
		int numHamstringPrimary = 0;
		int numQuadPrimary = 0;
		int numCalfPrimary = 0;
		int numShoulderPrimary = 0;
		int numTricepPrimary = 0;
		int numBicepPrimary = 0;
		int numAbsPrimary = 0;
		
		int numChestSecondary = 0;
		int numLatSecondary = 0;
		int numHamstringSecondary = 0;
		int numQuadSecondary = 0;
		int numCalfSecondary = 0;
		int numShoulderSecondary = 0;
		int numTricepSecondary = 0;
		int numBicepSecondary = 0;
		int numAbsSecondary = 0;
		
		boolean isFullWorkout = true;				
		boolean isWorkout = false;
		//Randomly get and add exercises until workout generated
		while (!isWorkout && isFullWorkout){
			boolean primaryFull = false;
			//End if muscle counts reached but suboptimal plan generated
			if(fileList.isEmpty()){
				isFullWorkout = false;
			} else {
			
			int linePoll = numLines.pollFirst();
			File filePoll = fileList.pollFirst();
			boolean isCompatible = true;
			Exercise randExercise = null;

			//Get random "strength" exercise
			while (randExercise == null || !randExercise.type.equals("Strength")){ 

			int exerciseNum = (int) (Math.random() * (linePoll - 1));
			if (exerciseNum % 2 == 1){
				exerciseNum--;
			}
			BufferedReader listReader = new BufferedReader(new FileReader(filePoll));
			String exercise = "";
			while (exerciseNum >= 0){
				exercise = listReader.readLine();
				exerciseNum--;
			}
			String exerciseLink = listReader.readLine();		
			randExercise = new Exercise (exercise, exerciseLink);
			listReader.close();
			}
			
			String primaryMuscle = randExercise.primaryMuscle;
			//Put all secondary muscles into combined string
			String secondaryMuscle = "";
			int index = 0;
			while (index < 12){
				secondaryMuscle = secondaryMuscle + randExercise.secondaryMuscles[index];
				index++;
			}
			
			//Check exercise for routine compatibility
			//Compare primary muscle compatibility			
			if (primaryMuscle.equals("Shoulders")){
				if (numShoulderPrimary > 0 && numShoulderSecondary > 3){
					isCompatible = false;
				} else if (numShoulderPrimary > 1){
					isCompatible = false;
				}
			} else if (primaryMuscle.equals("Abdominals")){
				if (numAbsPrimary > 1){
					isCompatible = false;
				} else if (numAbsPrimary > 0 && numAbsSecondary > 3) {
					isCompatible = false;
				} 
			} else if (primaryMuscle.equals("Quadriceps")){
				if (numQuadPrimary > 1){
					isCompatible = false;
				}
			} else if (primaryMuscle.equals("Chest")){
				if (numChestPrimary > 1 && numChestSecondary > 1){
					isCompatible = false;
				} else if (numChestPrimary > 2) {
					isCompatible = false;
				}
			} else if (primaryMuscle.equals("Triceps")){
				if (numTricepPrimary > 0){
					isCompatible = false;
				}
			} else if (primaryMuscle.equals("Biceps")){
				if (numBicepPrimary > 0){
					isCompatible = false;
				}
			} else if (primaryMuscle.equals("Lats")){
				if (numLatPrimary > 1){
					isCompatible = false;
				}
			}  else if (primaryMuscle.equals("Hamstrings")){
				if (numHamstringPrimary > 0){
					isCompatible = false;
				}
			} else if (primaryMuscle.equals("Calves")){
				if (numCalfPrimary > 0){
					isCompatible = false;
				}
			}
			
			//No further primary muscle exercises can be added for current muscle
			if(isCompatible == false){
				primaryFull = true;
			}
			
			//Compare secondary muscle compatibility
			if (isCompatible){
			if (secondaryMuscle.contains("Shoulders")){
				if (numShoulderPrimary > 1 && numShoulderSecondary > 2){
					isCompatible = false;
				} else if (numShoulderSecondary > 3){
					isCompatible = false;
				}
			} 
			if (isCompatible && secondaryMuscle.contains("Abdominals")){
				if(numAbsSecondary > 3){
					isCompatible = false;
				} else if (numAbsPrimary > 2){
					isCompatible = false;
				} else if (numAbsPrimary > 1 && numAbsSecondary > 1){
					isCompatible = false;
				}
			}  
			if (isCompatible && secondaryMuscle.contains("Quadriceps")){
				if (numQuadSecondary > 2){
					isCompatible = false;
				}
			}
			if(isCompatible && secondaryMuscle.contains("Chest")){
				if (numChestPrimary > 2){
					isCompatible = false;
				} else if (numChestPrimary > 1 && numChestSecondary > 1){
					isCompatible = false;
				} else if (numChestSecondary > 2){
					isCompatible = false;
				}
			}
			if (isCompatible && secondaryMuscle.contains("Triceps")){
				if (numTricepSecondary > 2){
					isCompatible = false;
				}
			}
			if (isCompatible && secondaryMuscle.contains("Biceps")){
				if (numBicepSecondary > 2){
					isCompatible = false;
				}
			}
			
			if (isCompatible && secondaryMuscle.contains("Lats")){
				if (numLatSecondary > 1){
					isCompatible = false;
				}
			}
			if (isCompatible && secondaryMuscle.contains("Hamstrings")){
				if (numHamstringSecondary > 2){
					isCompatible = false;
				}
			}  
			if (isCompatible && secondaryMuscle.contains("Calves")){
				if (numCalfSecondary > 2){
					isCompatible = false;
				}
			} 
		}
			
			
			if (isCompatible){
				//Increment and add new primary muscle
				 if (primaryMuscle.equals("Shoulders")){
						shoulderPrimary[numShoulderPrimary] = randExercise;
						numShoulderPrimary++;
				}  else if (primaryMuscle.equals("Abdominals")){
					absPrimary[numAbsPrimary] = randExercise;
					numAbsPrimary++;
				}  else if (primaryMuscle.equals("Quadriceps")){
					quadPrimary[numQuadPrimary] = randExercise;
					numQuadPrimary++;
				}   else if (primaryMuscle.equals("Chest")){
					chestPrimary[numChestPrimary] = randExercise;
					numChestPrimary++;
				}  else if (primaryMuscle.equals("Triceps")){
					tricepPrimary[numTricepPrimary] = randExercise;
					numTricepPrimary++;
				}  else if (primaryMuscle.equals("Biceps")){
					bicepPrimary[numBicepPrimary] = randExercise;
					numBicepPrimary++;
				} else if (primaryMuscle.equals("Lats")){
					latPrimary[numLatPrimary] = randExercise;
					numLatPrimary++;
				} else if (primaryMuscle.equals("Hamstrings")){
					hamstringPrimary[numHamstringPrimary] = randExercise;
					numHamstringPrimary++;
				}else if (primaryMuscle.equals("Calves")){
					calfPrimary[numCalfPrimary] = randExercise;
					numCalfPrimary++;
				}
				//Increment new secondary muscles
				 if (secondaryMuscle.contains("Shoulders")){
						numShoulderSecondary++;
				}  
				 if (secondaryMuscle.contains("Abdominals")){
					numAbsSecondary++;
				} 
				 if (secondaryMuscle.contains("Quadriceps")){
					numQuadSecondary++;
				} 
				 if (secondaryMuscle.contains("Chest")){
					numChestSecondary++;
				} 
				 if (secondaryMuscle.contains("Triceps")){
					numTricepSecondary++;
				} 
				 if (secondaryMuscle.contains("Biceps")){
					numBicepSecondary++;
				} 
				 if (secondaryMuscle.contains("Lats")){
					numLatSecondary++;
				} 
				 if (secondaryMuscle.contains("Hamstring")){
					numHamstringSecondary++;
				} 
				 if (secondaryMuscle.contains("Calves")){
					numCalfSecondary++;
				}
				
				//Check if workout is completed
				int muscleSum = numChestPrimary + numLatPrimary + numHamstringPrimary + numQuadPrimary 
						+ numCalfPrimary + numShoulderPrimary + numTricepPrimary + numBicepPrimary
						+ numChestSecondary + numLatSecondary + numHamstringSecondary 
						+ numQuadSecondary + numCalfSecondary + numShoulderSecondary 
						+ numTricepSecondary + numBicepSecondary;
				if (muscleSum >= 24){
					if ((numChestPrimary > 1 && numLatPrimary > 1 && numHamstringPrimary > 0
					     && numQuadPrimary > 1 && numCalfPrimary > 0 && numShoulderPrimary > 0
						 && numTricepPrimary > 0 && numBicepPrimary > 0 && numAbsPrimary > 0) 
						 || muscleSum >= 31){
						isWorkout = true;
					}
				}
			}

			//Remove filled primary muscle group from search
			if (!primaryFull){
				numLines.addLast(linePoll);
				fileList.addLast(filePoll);
			}
			}
		}
		
		//Print Routine if full workout generated
		if (isWorkout){
			int modInt = (int) (Math.random() * 200);
			//Print Monday/Thursday routine
			System.out.println("Monday/Thursday Routine (Chest, Tricep, Shoulder, Abs)");
			if (numChestPrimary == 2 && numTricepPrimary == 1 && numAbsPrimary == 1 && numShoulderPrimary == 1){
				System.out.println(chestPrimary[numChestPrimary - 1].name);
				numChestPrimary--;
			}
			//Print all associated primary muscle exercises in cyclical order
			while (numChestPrimary > 0 || numTricepPrimary > 0 || numAbsPrimary > 0 || numShoulderPrimary > 0){
				if ((modInt % 4) == 0 && numChestPrimary > 0){
					System.out.println(chestPrimary[numChestPrimary - 1].name);
					numChestPrimary--;
					modInt--;
				} else if ((modInt % 4) == 1 && numTricepPrimary > 0) {
					System.out.println(tricepPrimary[numTricepPrimary - 1].name);
					numTricepPrimary--;
					modInt--;
				} else if ((modInt % 4) == 2 && numAbsPrimary > 0) {
					System.out.println(absPrimary[numAbsPrimary - 1].name);
					numAbsPrimary--;
					modInt--;
				} else if ((modInt % 4) == 3 && numShoulderPrimary > 0) {
					System.out.println(shoulderPrimary[numShoulderPrimary - 1].name);
					numShoulderPrimary--;
					modInt--;
				} else {
					modInt--;
				}
			}
			System.out.println();
			//Print Tuesday/Friday routine
			System.out.println("Tuesday/Friday Routine (Legs, Back, Bicep)");
			//Print all associated primary muscle exercises in cyclical order
			while (numLatPrimary > 0 || numHamstringPrimary > 0 || numCalfPrimary > 0 || numBicepPrimary > 0 || numQuadPrimary > 0){
				if ((modInt % 5) == 0 && numLatPrimary > 0){
					System.out.println(latPrimary[numLatPrimary - 1].name);
					numLatPrimary--;
					modInt--;
				} else if ((modInt % 5) == 1 && numQuadPrimary > 0) {
					System.out.println(quadPrimary[numQuadPrimary - 1].name);
					numQuadPrimary--;
					modInt--;
				}  else if ((modInt % 5) == 2 && numHamstringPrimary > 0) {
					System.out.println(hamstringPrimary[numHamstringPrimary - 1].name);
					numHamstringPrimary--;
					modInt--;
				}  else if ((modInt % 5) == 3 && numBicepPrimary > 0) {
					System.out.println(bicepPrimary[numBicepPrimary - 1].name);
					numBicepPrimary--;
					modInt--;
				}  else if ((modInt % 5) == 4 && numCalfPrimary > 0) {
					System.out.println(calfPrimary[numCalfPrimary - 1].name);
					numCalfPrimary--;
					modInt--;
				}  else {
					modInt--;
				}
			}
	}
	return isWorkout;
	}	
}