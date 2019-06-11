package com.example.MapDetector;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.example.MapDetector.ui.main.roads;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CustomCap;
import com.google.android.gms.maps.model.Dash;
import com.google.android.gms.maps.model.Dot;
import com.google.android.gms.maps.model.Gap;
import com.google.android.gms.maps.model.JointType;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PatternItem;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.RoundCap;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.opencsv.CSVReader;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileReader;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.example.MapDetector.R.id.map;


/**
 * An activity that displays a Google map with polylines to represent paths or routes,
 * and polygons to represent areas.
 */
public class PolyActivity extends AppCompatActivity
        implements
        OnMapReadyCallback,
        GoogleMap.OnPolylineClickListener {

    private static final int COLOR_BLACK_ARGB = 0xff000000;
    private static final int COLOR_WHITE_ARGB = 0xffffffff;
    private static final int COLOR_GREEN_ARGB = 0xff388E3C;
    private static final int COLOR_PURPLE_ARGB = 0xff81C784;
    private static final int COLOR_ORANGE_ARGB = 0xffF57F17;
    private static final int COLOR_BLUE_ARGB = 0xffF9A825;

    private static final int POLYLINE_STROKE_WIDTH_PX = 12;
    private static final int POLYGON_STROKE_WIDTH_PX = 8;
    private static final int PATTERN_DASH_LENGTH_PX = 20;
    private static final int PATTERN_GAP_LENGTH_PX = 20;
    private static final PatternItem DOT = new Dot();
    private static final PatternItem DASH = new Dash(PATTERN_DASH_LENGTH_PX);
    private static final PatternItem GAP = new Gap(PATTERN_GAP_LENGTH_PX);

    // Create a stroke pattern of a gap followed by a dot.
    private static final List<PatternItem> PATTERN_POLYLINE_DOTTED = Arrays.asList(GAP, DOT);
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String From = "";
    int roadNumber =0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Retrieve the content view that renders the map.
        setContentView(R.layout.activity_maps);

        roadNumber=  getIntent().getExtras().getInt("roadNumebr");
        // Get the SupportMapFragment and request notification when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(map);
        mapFragment.getMapAsync(this);
    }

    /**
     * Manipulates the map when it's available.
     * The API invokes this callback when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera.
     * In this tutorial, we add polylines and polygons to represent routes and areas on the map.
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onMapReady(GoogleMap googleMap) {
        road(loadStop(googleMap),roadNumber);
    }

    private GoogleMap loadStop(GoogleMap googleMap) {
        try {
            InputStream mng = getResources().openRawResource(R.raw.hajez);
            File file = new File(getFilesDir(), "hajze.csv");
            FileUtils.copyInputStreamToFile(mng, file);
            CSVReader reader = new CSVReader(new FileReader(file));
            List<String[]> entary = reader.readAll();
            mng.close();
            ;
            reader.close();
            MarkerOptions markerOptions = new MarkerOptions();
            for (String[] x : entary) {
                markerOptions.position(new LatLng(Double.parseDouble(x[2]), Double.parseDouble(x[1])));
                markerOptions.title(x[0]);
                googleMap.addMarker(markerOptions);
            }
        } catch (Exception ex) {

        }
        return googleMap;
    }

    private GoogleMap road(GoogleMap googleMap, int roadNumber) {
        try {
            Map<Integer, Integer> roads = new HashMap<Integer, Integer>();
            roads.put(1, R.raw.road1);
            roads.put(2, R.raw.road2);
            roads.put(3, R.raw.road3);
            roads.put(4, R.raw.road4);

            InputStream mng = getResources().openRawResource(roads.get(roadNumber));
            File file = new File(getFilesDir(), "road" + roadNumber + ".csv");
            FileUtils.copyInputStreamToFile(mng, file);
            CSVReader reader = new CSVReader(new FileReader(file));
            List<String[]> entary = reader.readAll();
            mng.close();
            ;
            reader.close();
            List<LatLng> lat = new ArrayList<>();

            for (String[] x : entary) {
                lat.add(new LatLng(Double.parseDouble(x[1]), Double.parseDouble(x[2])));
            }
            // Polylines are useful to show a route or some other connection between points.
            Polyline polyline1 = googleMap.addPolyline(new PolylineOptions().color(Color.BLUE)
                    .clickable(true)
                    .addAll(lat));
            // Store a data object with the polyline, used here to indicate an arbitrary type.
            polyline1.setTag("A");
            // Style the polyline.
            stylePolyline(polyline1);
            googleMap.moveCamera(
                    CameraUpdateFactory.
                            newLatLngZoom(lat.get(0), 11));


        } catch (Exception ex) {

        }
        return googleMap;
    }


    /**
     * Styles the polyline, based on type.
     *
     * @param polyline The polyline object that needs styling.
     */
    private void stylePolyline(Polyline polyline) {
        String type = "";
        // Get the data object stored with the polyline.
        if (polyline.getTag() != null) {
            type = polyline.getTag().toString();
        }

        switch (type) {
            // If no type is given, allow the API to use the default.
            case "A":
                polyline.setColor(Color.BLUE);
                // Use a custom bitmap as the cap at the start of the line.
                polyline.setStartCap(
                        new CustomCap(
                                BitmapDescriptorFactory.fromResource(R.drawable.ic_arrow), 10));
                break;
            case "B":
                // Use a round cap at the start of the line.
                polyline.setStartCap(new RoundCap());
                break;
        }

        polyline.setEndCap(new RoundCap());
        polyline.setWidth(POLYLINE_STROKE_WIDTH_PX);
        polyline.setColor(COLOR_BLACK_ARGB);
        polyline.setJointType(JointType.ROUND);
    }


    /**
     * Listens for clicks on a polyline.
     *
     * @param polyline The polyline object that the user has clicked.
     */
    @Override
    public void onPolylineClick(Polyline polyline) {
        // Flip from solid stroke to dotted stroke pattern.
        if ((polyline.getPattern() == null) || (!polyline.getPattern().contains(DOT))) {
            polyline.setPattern(PATTERN_POLYLINE_DOTTED);
        } else {
            // The default pattern is a solid stroke.
            polyline.setPattern(null);
        }

        Toast.makeText(this, "Route type " + polyline.getTag().toString(),
                Toast.LENGTH_SHORT).show();
    }

}
