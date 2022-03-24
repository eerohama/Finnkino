package com.example.finnkino;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private Spinner spinner;
    private EditText editPvm;
    private EditText editStartTime;
    private EditText editFinishTime;
    private EditText editMoviename;
    private Context context;
    private String firstNode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = MainActivity.this;
        spinner = (Spinner) findViewById(R.id.spinner);
        editPvm = (EditText) findViewById(R.id.editPvm);
        editStartTime = (EditText) findViewById(R.id.editStart_time);
        editFinishTime = (EditText) findViewById(R.id.editFinishTime);
        editMoviename = (EditText) findViewById(R.id.editMoviename);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        Document doc = readXML("https://www.finnkino.fi/xml/TheatreAreas/");
        createList(doc);
        dropdownMenu();
    }

    public void dropdownMenu(){
        TheaterList tList = TheaterList.getInstance();
        ArrayList<String> spinnerList = new ArrayList<String>();

        for(Theater t : tList.getList()){
            spinnerList.add(t.getName());
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,   android.R.layout.simple_spinner_dropdown_item, spinnerList);
        spinner.setAdapter(adapter);
    }

    public Document readXML(String urlString) {
        try {
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document doc = builder.parse(urlString);
            doc.getDocumentElement().normalize();
            return doc;

        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } finally {
            System.out.println("<<<<< XML tiedosto luettu >>>>>");
        }
        return null;
    }

    public void createList(Document doc){
        TheaterList theaterList = TheaterList.getInstance();
        NodeList nList = doc.getDocumentElement().getElementsByTagName("TheatreArea");

        for(int i = 0; i < nList.getLength(); i++){
            Node node = nList.item(i);
            if(node.getNodeType() == Node.ELEMENT_NODE){

                Element element = (Element) node;
                String s_id = element.getElementsByTagName("ID").item(0).getTextContent();
                int t_id = Integer.parseInt(s_id);
                String t_name = element.getElementsByTagName("Name").item(0).getTextContent();
                Theater t = new Theater(t_name, t_id);
                theaterList.addToList(t);
                if(i == 0){
                    firstNode = t_name;
                }
            }
        }
    }

    public void makeUrl(View view) {
        TheaterList tList = TheaterList.getInstance();
        Movies mList = Movies.getInstance();
        mList.clearList();

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        SimpleDateFormat newFormat = new SimpleDateFormat("HH:mm"); //dd.MM.yyyy
        Date date_start = null;
        Date date_end = null;
        Calendar cal1 = null;
        Calendar cal2 = null;
        Boolean findByName = false;

        String alku = editStartTime.getText().toString();
        String loppu = editFinishTime.getText().toString();
        String pvm = editPvm.getText().toString();
        String theater = spinner.getSelectedItem().toString();
        String movieName = editMoviename.getText().toString();
        mList.setHeader(theater + "  " + pvm);
        int theater_id = tList.getID(theater);

        if(movieName.length() > 0){
            findByName = true;
            mList.setHeader(movieName);
        }

        // testCase:n arvo 0, kun alku ja loppu tekstikentät ovat molemmat tyhjiä, 1 kun vain alku sisältää ajan,
        // 2 kun vain loppu sisältää ajan ja 3 kun molemmat kentät sisältävät ajan.
        int testCase = 0;
        if(alku.length() > 0){
            cal1 = transformTime(alku);
            testCase = 1;
        }
        if(loppu.length() > 0){
            cal2 = transformTime(loppu);
            testCase = 2;
        }
        if(alku.length() > 0 && loppu.length() > 0){
            testCase = 3;
        }


        String[] mainAreas = {"Pääkaupunkiseutu","Jyväskylä: FANTASIA","Kuopio: SCALA","Lahti: KUVAPALATSI","Lappeenranta: STRAND","Oulu: PLAZA","Pori: PROMENADI","Tampere","Turku: KINOPALATSI"};
        String url;
        String temp = theater;

        for(int i = 0; i < mainAreas.length; i++){
            if(firstNode.equals(temp)){
                theater = mainAreas[i];
                theater_id = tList.getID(theater);
            }

            url = "https://www.finnkino.fi/xml/Schedule/?area="+theater_id+"&dt="+pvm;
            Document doc = readXML(url);

            NodeList nList = doc.getDocumentElement().getElementsByTagName("Show");
            for(int j = 0; j < nList.getLength(); j++){
                Node node = nList.item(j);
                if(node.getNodeType() == Node.ELEMENT_NODE){
                    Element element = (Element) node;
                    String name = element.getElementsByTagName("Title").item(0).getTextContent();
                    String showStart = element.getElementsByTagName("dttmShowStart").item(0).getTextContent();
                    String showEnd = element.getElementsByTagName("dttmShowEnd").item(0).getTextContent();
                    try {
                        date_start = format.parse(showStart);
                        date_end = format.parse(showEnd);
                    } catch (ParseException e){
                        Log.e("ParseException", "Virhe päivämäärän käsittelyssä.");
                    }
                    String startTime = newFormat.format(date_start);
                    String endTime = newFormat.format(date_end);

                    switch(testCase){
                        case 0:
                            if(findByName == true){
                                if(movieName.equals(name)){
                                    mList.addToList(theater+"   "+startTime+ " - " +endTime);
                                }
                            } else {
                                mList.addToList(name+"   "+startTime+ " - " +endTime);
                            }
                            break;
                        case 1:
                            if(transformTime(startTime).after(cal1)){
                                if(findByName == true){
                                    if(movieName.equals(name)){
                                        mList.addToList(theater+"   "+startTime+ " - " +endTime);
                                    }
                                } else {
                                    mList.addToList(name+"   "+startTime+ " - " +endTime);
                                }
                            }
                            break;
                        case 2:
                            if(transformTime(endTime).before(cal2)){
                                if(findByName == true){
                                    if(movieName.equals(name)){
                                        mList.addToList(theater+"   "+startTime+ " - " +endTime);
                                    }
                                } else {
                                    mList.addToList(name+"   "+startTime+ " - " +endTime);
                                }
                            }
                            break;
                        case 3:
                            if( transformTime(startTime).after(cal1) && transformTime(endTime).before(cal2)){
                                if(findByName == true){
                                    if(movieName.equals(name)){
                                        mList.addToList(theater+"   "+startTime+ " - " +endTime);
                                    }
                                } else {
                                    mList.addToList(name+"   "+startTime+ " - " +endTime);
                                }
                            }
                            break;
                    }
                }
            }
            if(firstNode.equals(temp) == false){
                break;
            }
        }
        openListView();
    }


    public Calendar transformTime(String time){
        // Lähde: https://stackoverflow.com/questions/10086053/comparing-hours-in-java

        String[] parts = time.split(":");
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(parts[0]));
        cal.set(Calendar.MINUTE, Integer.parseInt(parts[1]));
        return cal;
    }

    // Avaa ListView:n uuteen Activity ikkunaan.
    public void openListView(){
        Intent intent = new Intent(this,Activity2.class);
        startActivity(intent);
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}