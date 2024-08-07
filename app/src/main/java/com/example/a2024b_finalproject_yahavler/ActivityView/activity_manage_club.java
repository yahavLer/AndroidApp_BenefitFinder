package com.example.a2024b_finalproject_yahavler.ActivityView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import com.example.a2024b_finalproject_yahavler.Adapter.ClubAdapter;
import com.example.a2024b_finalproject_yahavler.DataManagers.ClubManager;
import com.example.a2024b_finalproject_yahavler.Managers.AppManagerFirebase;
import com.example.a2024b_finalproject_yahavler.Managers.NevigationActivity;
import com.example.a2024b_finalproject_yahavler.Model.Club;
import com.example.a2024b_finalproject_yahavler.Model.ClubMembership;
import com.example.a2024b_finalproject_yahavler.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class activity_manage_club extends AppCompatActivity implements ClubAdapter.OnClubClickListener {
    private RecyclerView main_LST_club;
    private ArrayList<Club> clubs = new ArrayList<>();
    private CardView clubDetailsCard;
    private EditText tvClubCardNumber, tvClubExpiryDate;
    private Button saveButton;
    private String selectedClubId = "";
    private String currentUserId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.manage_clubs);
        findView();
        initAllClubs();
        NevigationActivity.findNevigationButtens(this);
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            currentUserId = currentUser.getUid();
            // השתמשי במשתנה currentUserId לכל שאר הפונקציות
        } else {
            // המשתמש לא מחובר, יש לטפל במצב זה
        }
    }

    @Override
    public void onClubClick(Club club) {
        selectedClubId = club.getClubId();
    }

    @Override
    public void onSaveClubMembership(String clubId, String cardNumber, String expiryDate) {
        Date parsedExpiryDate = null;
        try {
            parsedExpiryDate = new SimpleDateFormat("MM/yyyy", Locale.getDefault()).parse(expiryDate);
        } catch (ParseException e) {
            e.printStackTrace();
            return;
        }
        // יצירת ה-ClubMembership החדש
        ClubMembership newMembership = new ClubMembership(currentUserId, clubId, cardNumber, parsedExpiryDate);
        // עדכון רשימת החברות של המשתמש ב-Firebase
        AppManagerFirebase.addClubMembership(newMembership, currentUserId, success -> {
            if (success) {
                Toast.makeText(this, "Club added successfully", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Failed to update club membership", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void findView() {
        main_LST_club = findViewById(R.id.clubs_recycler_view);
        clubDetailsCard = findViewById(R.id.CV_club_details);
        tvClubCardNumber = findViewById(R.id.TV_club_card_number);
        tvClubExpiryDate = findViewById(R.id.TV_club_expiry_date);
        saveButton = findViewById(R.id.save_button);
    }

    private void initAllClubs() {
        AppManagerFirebase.fetchAllClubs(new AppManagerFirebase.CallBack<ArrayList<Club>>() {
            @Override
            public void res(ArrayList<Club> allClubs) {
                if (allClubs != null) {
                    clubs = allClubs;
                    ClubAdapter clubAdapter = new ClubAdapter(clubs, activity_manage_club.this, activity_manage_club.this);
                    main_LST_club.setAdapter(clubAdapter);
                }
            }
        });
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        main_LST_club.setLayoutManager(linearLayoutManager);
    }


    private Date parseDate(String dateStr) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        try {
            return dateFormat.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }
    @Override
    protected void onPause() {
        super.onPause();
        Log.d("Lifecycle", "onPause called");
        // Save any necessary state here
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("Lifecycle", "onResume called");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("Lifecycle", "onDestroy called");
        // Clean up any resources here
    }
}
