import java.sql.SQLOutput;
import java.util.List;
import java.util.Scanner;

public class ModelSelection {
    public static String selectModel(
            List<String> models,
            Scanner scanner
    ){
        if(models.isEmpty()){
            throw new IllegalArgumentException("No models available");
        }

        System.out.println("Available Models");

        for(int i = 0; i<models.size(); i++){
            System.out.println((i+1) + ". " + models.get(i));
        }


        while(true){

            System.out.println("Selected Model");

            try{
                int choice = Integer.parseInt(scanner.nextLine());

                if(choice >=1 && choice <= models.size()){
                    return models.get(choice-1);
                }
            } catch (NumberFormatException e){
                System.out.println("Please enter a valid number");
            }
        }
    }


}
