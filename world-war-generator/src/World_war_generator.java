import java.io.*;
import java.util.Random;
import java.util.Scanner;

/*

 */

public class World_war_generator {

    public static void main(String[] args) {
        //Variables
        String[][] countries = new String[0][0]; //Buffer for storing countries names. First dimension is for province and second for owner.
        int[] provinces_left = new int[0]; //This array represents how many provinces each country have.
        int count = 0; //Number of countries based on how many of them are in the file.
        int left = 0; //Number of countries remained.
        int interval = 5; //Variable that controls after how many battles there will be pause.
        int winner = 0; //Index of the winner!
        Scanner input = new Scanner(System.in);
        byte choice = 0;
        //-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_

        //Reading names of countries from the file.
        try (RandomAccessFile countries_read = new RandomAccessFile("countries.txt","r")) {
            while (countries_read.readLine() != null) count++;
            left = count;
            countries_read.seek(0);
            provinces_left = new int[count];
            countries = new String[count][2];

            String buffer;
            for (int i = 0; i < count; i++) {
                buffer = countries_read.readLine();
                countries[i][0] = buffer;
                countries[i][1] = buffer;
                provinces_left[i] = 1;
            }

        }catch(FileNotFoundException err){
            System.out.println("File not found!");
        }catch (IOException err) {
            System.out.println("Some unexpected error occured, program will close.");
            err.printStackTrace();
            return;
        }

        //Main loop of the simulation
        System.out.print("Welcome in totally random World War generator where nothing have sense and wars never end!\n Options avaiable:\n");
        while(left != 1){
            menu_text(interval);
            if(input.hasNextByte()){
                choice = input.nextByte();
            }
            interval = menu(countries,provinces_left,choice,interval,count);
            if(interval == -2){
                return;
            }
            for(int i = interval; (i > 0 && choice < 4) || (interval == -1 && left != 1); i--){
                left += single_war(countries,provinces_left,count);
            }
        }

        //Quick check of the winner...
        for(int i = 0; i < count; i++){
            if(provinces_left[i] > 0){
                winner = i;
                break;
            }
        }
        System.out.println(countries[winner][0] + " dominates the world!");
    }


    private static int single_war(String[][] countries, int[] provinces_left, int count) {
        Random generator = new Random();
        int attacker, defender; int result = 0;
        boolean flag_1 = true, flag_2 = true;
        do{
            attacker = generator.nextInt(count);
            defender = generator.nextInt(count);

        }while (countries[attacker][1].equals(countries[defender][1]));
        System.out.println(countries[attacker][1] + " counquers " + countries[defender][0] + " owned by " + countries[defender][1] + "!");

        for(int i = 0; i < count; i++){
            if(countries[defender][1].equals(countries[i][0])&& flag_1) {
                provinces_left[i]--;
                if (provinces_left[i] == 0) { //Deffender loses province and if he no longer have any he loses the war.
                    System.out.println("    " + countries[i][0] + " lossed all his provinces!");
                    result = -1;
                    flag_1 = false;
                }
            }
            else if(countries[attacker][1].equals(countries[i][0]) && flag_2){
                    provinces_left[i]++;
                    flag_2 = false;
            }
            else if(!flag_1 && !flag_2) break;
        }
        countries[defender][1] = countries[attacker][1];
        return result;
    }

    private static int menu(String[][] countries, int[] provinces_left, int choice, int interval, int count){
        switch(choice){
            case 1:
                int buffer;
                Scanner input = new Scanner(System.in);
                System.out.print("Write a new value: ");
                if(input.hasNextInt()){
                    buffer = input.nextInt();
                    if(buffer < 0){
                        System.out.println("Value can't be negative.");
                    } else interval = buffer;
                }
                break;
            case 2: break;
            case 3: interval = -1; break;
            case 4:
                for(int i = 0; i < count; i++){
                    if(provinces_left[i] != 0){
                        System.out.println(countries[i][0] + " : " + provinces_left[i]);
                    }
                } break;
            case 5:  interval = -2; break;
            default: System.out.println("There is no such operation.");
        }
        return interval;
    }

    private static void menu_text(int interval){
       System.out.println("     1. Change the interval after which program will stop. Current value: " + interval);
       System.out.println("     2. Fast forward to the next stop.");
       System.out.println("     3. Fast forward to the end of \"simulation\".");
       System.out.println("     4. Print list of all countries remaining and number of regions which they control.");
       System.out.println("     5. Quit");
       System.out.print("Select an option: ");
    }
}