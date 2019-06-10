package com.example.MapDetector.ui.main;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.example.MapDetector.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.opencsv.CSVReader;
import  com.example.MapDetector.Config.Message;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileReader;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

/**
 * A simple {@link Fragment} subclass.
 */
public class BugFragment extends Fragment {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
String Type= "";
String barrierName="";
    public BugFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
//        List<String> m = new ArrayList<String>();
//        List<roads> y = new ArrayList<roads>();
//
//        state s = new state(3,m);
//       m.add("jalazone");
//       m.add("za’tara");
//        roads road = new roads(3,s);
//        y.add(road);
//        m = new ArrayList<String>();
//        m.add("beit el");
//        m.add("za’tara");
//        s = new state(1,m);
//        road = new roads(1,s);
//        y.add(road);
//
//        m = new ArrayList<String>();
//        m.add("za’tara");
//        s = new state(2,m);
//        road = new roads(2,s);
//        y.add(road);
//        m = new ArrayList<String>();
//        m.add("za’tara");
//
//        s = new state(4,m);
//        road = new roads(4,s);
//        y.add(road);
//        Intent intent = new Intent();
//        CitesAndRoad cs = new CitesAndRoad("Ramallah,Nablus",y);
//        db.collection("roads").add(cs);
        View rootView = inflater.inflate(R.layout.fragment_bug, container, false);
        Spinner BugSpinner = rootView.findViewById(R.id.BugSpinner);
        Spinner BarrierSpinner = rootView.findViewById(R.id.BarrierSpinner);
        InputStream mng = getResources().openRawResource(R.raw.hajez);
        List<String> entary = new ArrayList<>();
        try{
            File file = new File(getActivity().getFilesDir(),"hajze.csv");
            FileUtils.copyInputStreamToFile(mng, file);
            CSVReader reader = new CSVReader(new FileReader(file));
            List<String[] >  x    = reader.readAll();
            for(String[] n : x){
                entary.add(n[0]);
            }
        }catch (Exception ex){

        }

        ArrayAdapter<String> barrier = new ArrayAdapter<String>(getActivity().getApplicationContext(),
                android.R.layout.simple_spinner_dropdown_item,entary);
        BarrierSpinner.setAdapter(barrier);
        BugSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Type =parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        BarrierSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                barrierName =parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        Button bt = rootView.findViewById(R.id.Report);
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final bugs myBug = new bugs(barrierName,Type);
                final Query x = db.collection("bug").whereEqualTo("Name",barrierName);
               x.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        System.out.println(task.getResult().getDocuments().size());
                       if( task.getResult().getDocuments().size() >0){
                            db.document("bug/"+task.getResult().getDocuments().get(0).getId()).set(myBug).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    System.out.println(e.getMessage());
                                }
                            }); }else{
                           db.collection("bug").add(myBug);
                           sendPush(myBug.Name+" "+myBug.BugName);

                       }
                    }
                }) ;



            }
        });
        return rootView;
    }
    private void sendPush(String mMessage){
        Message message = new Message();
        message.setUser_id("Admin");
        message.setMessage(mMessage);
        message.setTimestamp(getTimestamp());
        db.collection("messages").add(message);
    }
    /**
     * Return the current timestamp in the form of a string
     * @return
     */
    private String getTimestamp(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US);
        sdf.setTimeZone(TimeZone.getTimeZone("Canada/Pacific"));
        return sdf.format(new Date());
    }

}

