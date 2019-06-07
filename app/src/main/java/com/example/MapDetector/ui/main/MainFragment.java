package com.example.MapDetector.ui.main;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.MapDetector.PolyActivity;
import com.example.MapDetector.R;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

/**
 * A simple {@link Fragment} subclass.
 */
public class MainFragment extends Fragment {
    String From = "";
    String To = "";
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @RequiresApi(api = Build.VERSION_CODES.N)

    public MainFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        Spinner sp = rootView.findViewById(R.id.FromSpinner);
        Spinner sp2 = rootView.findViewById(R.id.ToSpinner);
        sp2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                To = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                From = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        Button bt = rootView.findViewById(R.id.findPath);
        bt.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                if (From == To) {
                    Toast.makeText(getActivity(), "Can find Path in same city", Toast.LENGTH_LONG).show();
                } else {
                    doInBackground();
                }
            }
        });
        return rootView;
    }

    AtomicReference<roads> takenRoad = new AtomicReference<roads>();

    @RequiresApi(api = Build.VERSION_CODES.N)
    void doInBackground() {
        onPreExecute();
        Task<QuerySnapshot> road;
        Task<QuerySnapshot> barrier;
        road = db.collection("roads").get();
        barrier = db.collection("bug").get();

        Task<QuerySnapshot> finalRoad = road;
        Task<QuerySnapshot> finalBarrier = barrier;
        Tasks.whenAllComplete(road, barrier).addOnCompleteListener(com -> {
            CitesAndRoad destination = null;
            List<CitesAndRoad> citesAndRoads = finalRoad.getResult().toObjects(CitesAndRoad.class);
            List<bugs> buge = finalBarrier.getResult().toObjects(bugs.class);
            for (CitesAndRoad city : citesAndRoads) {
                if (city.CityName.contains(From) && city.CityName.contains(To)) {
                    destination = city;
                    break;
                }
            }
            ;
            int max = Integer.MAX_VALUE;
            roads r = new roads();
            boolean flag = false;
            destination.numberRoad.sort((x, y) -> r.compare(x, y));
            for (roads x : destination.numberRoad) {
                for (String b : x.roadState.barrierName) {
                    for (bugs u : buge) {
                        if (b.equals( u.Name) && u.BugName != "") {
                            flag = true;
                            break;
                        }
                    }
                    if (flag) {
                        break;
                    }

                }
                if (!flag) {
                    takenRoad.set(x);
                    break;
                }
                flag=false;

            }
            onPostExecute(takenRoad.get() != null);

        });

    road=null;barrier=null;

    }

    ProgressDialog progressDialog;

    protected void onPostExecute(Boolean result) {
        if (progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
        int duration = Toast.LENGTH_SHORT;

        Toast toast;


        if (result) {
            CharSequence text = "Find "+ takenRoad.get().roadNumber;
            toast = Toast.makeText(getActivity(), text, duration);
            toast.show();
            Intent intent = new Intent(getActivity(), PolyActivity.class);
            Bundle args = new Bundle();
            args.putInt("roadNumebr", takenRoad.get().roadNumber);
            if (getActivity().getPackageManager() != null) {
                intent.putExtras(args);
                startActivity(intent);
            }
        } else {
            CharSequence text = "can't Find";
            toast = Toast.makeText(getActivity(), text, duration);
            toast.show();
        }
    }

    protected void onPreExecute() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage(" please wait...");
            progressDialog.show();
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.setCancelable(false);
        }
    }


}

