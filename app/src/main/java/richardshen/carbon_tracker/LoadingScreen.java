package richardshen.carbon_tracker;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class LoadingScreen extends AppCompatActivity {

    public static Thread t;
    private static final int waitTimeInMS = 4000;

    public static final ArrayList<CarCollection> allBrands = new ArrayList<>(10);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        requestWindowFeature(Window.FEATURE_NO_TITLE);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_loading_screen);

        Runnable m = new Runnable() {
            @Override
            public void run() {
                final ArrayList<String> filesFound = locateFileNames();
                Comparator<String> comparator = new Comparator<String>() {
                    @Override
                    public int compare(String o1, String o2) {
                        return o1.compareTo(o2);
                    }
                };

                Collections.sort(filesFound, comparator);

                for(String brand : filesFound) {
                    try {
                        InputStream is = getResources().openRawResource(getFileID(brand));
                        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                        reader.mark(1000);
                        String tempLine = reader.readLine();
                        reader.reset();

                        String[] tempFields = tempLine.split(",");
                        String brandName = tempFields[1];

                        CarCollection newCol = new CarCollection(brandName);

                        String line;

                        while ((line = reader.readLine()) != null) {
                            newCol.addCar(line);
                        }

                        allBrands.add(newCol);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        };

        t = new Thread(m);
        t.start();

        Handler handler = new Handler();
        handler.postDelayed(new Runnable(){
            @Override
            public void run(){
                Intent intent = new Intent(LoadingScreen.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        }, waitTimeInMS);
    }

    private Integer getFileID(String fileName) {
        Integer id = getResources().getIdentifier(fileName, "raw", this.getPackageName());
        return id;
    }

    private ArrayList<String> locateFileNames() {
        int id = getResources().getIdentifier("main", "raw", this.getPackageName());

        InputStream is = getResources().openRawResource(id);

        BufferedReader reader = new BufferedReader(new InputStreamReader(is));

        ArrayList<String> files = new ArrayList<>(50);


        try {
            String line = reader.readLine(); // Skip first line
            while((line = reader.readLine()) != null) {
                String[] fields = line.split(",");

                String fileName = fields[1].replaceAll("[^a-zA-Z0-9]", "");
                fileName = fileName.toLowerCase();

                if(!(files.contains(fileName))) {
                    files.add(fileName);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return files;
    }
}
