package com.example.w9;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class MainActivity extends AppCompatActivity {

    Context context;
    Button button;
    Spinner spinner, spinner2;
    ListView listView;
    EditText aika;
    EditText aika2;

    List<String> listOfDates = new ArrayList<>();
    TheatherHandling theatherHandling = TheatherHandling.getInstance();
    Theather theather = new Theather();



    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = MainActivity.this;

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        //Luodaan spinner teatterille
        spinner = findViewById(R.id.spinner);
        //Luodaan spinner päivämäärälle
        spinner2 = findViewById(R.id.spinner2);
        listView = (ListView) findViewById(R.id.listview);
        aika = findViewById(R.id.aika);
        aika2 = findViewById(R.id.aika2);

        List<Theather> theathers = theatherHandling.getList();
        List<String> locations = theatherHandling.getNames();

        ArrayAdapter<String> adapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_spinner_dropdown_item,locations);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            String location = parent.getSelectedItem().toString();
            int ID = theatherHandling.findID(location);
        }
        @Override
        public void onNothingSelected(AdapterView<?> parent) {
        }
    });
        getDate();
        ArrayAdapter<String> adapter2 = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_spinner_dropdown_item,listOfDates);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner2.setAdapter(adapter2);
        spinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String date = parent.getSelectedItem().toString();

            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void getDate(){
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        LocalDateTime now = LocalDateTime.now();
        listOfDates.add(String.valueOf(now.format(dateTimeFormatter)));

        for (int i = 0; i<10; i++){
            now = now.plusDays(1);
            listOfDates.add(String.valueOf(now.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))));
        }
    }
    public void getMovie(View v){
        ArrayList<String> data_list = new ArrayList<String>();
        String alkamisaika = aika.getText().toString();
        String viimeinenaika = aika.getText().toString();
        Date dt1,dt2;

        // Change the format of dates, in data format is 2021-03-22'T'21:00:00
        SimpleDateFormat format_in = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        SimpleDateFormat format_out = new SimpleDateFormat("HH:mm dd.MM.yyyy");
        SimpleDateFormat format_time = new SimpleDateFormat("HH:mm");

        Date dt = null;

        String location = spinner.getSelectedItem().toString();
        int ID = theatherHandling.findID(location);
        String date = spinner2.getSelectedItem().toString();

        try {

            DocumentBuilder dBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            String urlString = "https://www.finnkino.fi/xml/Schedule/?area="+ID+"&dt="+date;
            Document doc = dBuilder.parse(urlString);
            doc.getDocumentElement().normalize();
            System.out.println("Root element: " + doc.getDocumentElement().getNodeName());

            NodeList nList = doc.getDocumentElement().getElementsByTagName("Show");
            System.out.println(nList.getLength());

            for (int i = 0; i < nList.getLength() ; i++) {
                Node node = nList.item(i);

                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) node;
                    String Title = element.getElementsByTagName("Title").item(0).getTextContent();
                    String start = element.getElementsByTagName("dttmShowStart").item(0).getTextContent();
                    try {
                        dt = format_in.parse(start);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    String start_time = format_out.format(dt);

                    String line = Title + "\nElokuva alkaa: " + start_time;
                    data_list.add(line);

                    //if (dt.after(dt1) && dt.before(dt2)){data_list.add(line);}
                }
            }
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            System.out.println(data_list);
            System.out.println("Suoritettu");
        }
        ArrayAdapter<String> linesAdapter = new ArrayAdapter(MainActivity.this, android.R.layout.simple_list_item_1, data_list);
        listView.setAdapter(linesAdapter);
    }
}