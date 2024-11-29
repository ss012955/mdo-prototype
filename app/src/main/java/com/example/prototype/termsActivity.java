package com.example.prototype;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

import Adapters.TermsAdapter;

public class termsActivity extends AppCompatActivity {
    TabLayout tabLayout;
    private ImageView chatImageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_terms);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        RecyclerView recyclerView = findViewById(R.id.recyclerViewTerms);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        chatImageView = findViewById(R.id.chat);
        tabLayout = findViewById(R.id.tablayout);
        chatImageView.setOnClickListener(v -> {

            Intent intent = new Intent(this, ChatActivity.class);
            startActivity(intent);
        });
        int[] icons = {R.drawable.home, R.drawable.user_journal, R.drawable.profile};
        for (int i = 0; i < icons.length; i++) {
            tabLayout.addTab(tabLayout.newTab().setIcon(icons[i]));
        }
        tabLayout.selectTab(null);
        // Reset the tab icons to their unselected state
        for (int i = 0; i < tabLayout.getTabCount(); i++) {
            TabLayout.Tab tab = tabLayout.getTabAt(i);
            if (tab != null) {
                tab.setIcon(icons[i]); // Reset to the original icon (unselected)
            }
        }
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                startActivity(new Intent(termsActivity.this, home.class)
                        .putExtra("tab_position", tab.getPosition()));
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
        // Prepare terms data
        List<String> termsList = new ArrayList<>();
        termsList.add("1. Acceptance of Terms\n By accessing and using the UMak Medical and Dental Office Booking Appointment System (hereafter referred to as “UMak Medical and Dental HealthHub”), you acknowledge that you have read, understood, and agree to be bound by these Terms and Conditions, as well as our Privacy Policy. If you do not agree with these Terms, you should not access or use UMak MDO HealthHub.");
        termsList.add("2. Eligibility\nUMak MDO HealthHub is intended for the exclusive use of the students, staff, and affiliates of - For any questions regarding these Terms and Conditions, please contact the UMak Medical and Dental Office through our official social media channels or via email.the University of Makati (UMak). Users must be at least 18 years of age or have the necessary permissions if underage. By using UMak MDO HealthHub, you represent that you meet these eligibility criteria.");
        termsList.add("3. User Responsibilities\n- Accurate Information: Users must provide accurate, current, and complete personal information when registering and booking appointments. It is the user’s responsibility to ensure that this information is kept up-to-date.\n- Appointment Bookings: All appointments made through UMak MDO HealthHub must be for legitimate and authorized purposes, such as medical or dental consultations. Any misuse of UMak MDO HealthHub, such as booking multiple unnecessary appointments, may result in the suspension of access.\n- Login Credentials: You are responsible for maintaining the confidentiality of your account credentials. The UMak Medical and Dental Office (hereafter “the Office”) is not responsible for any unauthorized access to your account. You should promptly notify the Office of any unauthorized use of your account.");
        termsList.add("4. Use of UMak MDO HealthHub\n- Permissible Use: UMak MDO HealthHub is provided solely for the purpose of booking appointments with the Medical and Dental Office at UMak. Any other use, such as data scraping, hacking, or unauthorized access to system resources, is strictly prohibited.\n- Account Creation and Login: Users must create an account to use UMak MDO HealthHub. You agree to keep your login credentials confidential and ensure the security of your account.\n- Service Availability: While the Office will strive to ensure that UMak MDO HealthHub is available at all times, there may be occasional downtimes due to maintenance or unforeseen technical issues. The Office will not be liable for any disruptions in service.");
        termsList.add("5. Account Termination\n - The Office reserves the right to suspend or terminate your access to UMak MDO HealthHub at any time, without notice, for conduct that violates these Terms and Conditions or is otherwise deemed inappropriate, including but not limited to fraud, system abuse, or providing false information.");
        termsList.add("6. Modifications to UMak MDO HealthHub and Terms\n- The Office reserves the right to modify or discontinue any aspect of UMak MDO HealthHub at any time, including these Terms and Conditions, without prior notice. Changes will take effect immediately upon posting. Users are encouraged to regularly review these Terms to ensure they remain in compliance.\n- Users will be notified of significant changes to these Terms via email and/or announcements within UMak MDO HealthHub.");
        termsList.add("7. Limitation of Liability\n- The UMak Medical and Dental Office shall not be liable for any direct, indirect, incidental, or consequential damages arising from your use or inability to use UMak MDO HealthHub. The Office is not responsible for any data loss or unauthorized access resulting from the user’s failure to secure their login credentials.\n- UMak MDO HealthHub and its contents are provided on an \"as-is\" basis without any warranties of any kind, either express or implied.");
        termsList.add("8. Privacy and Confidentiality\n - All personal information submitted through UMak MDO HealthHub is governed by the Privacy Policy, which forms part of these Terms and Conditions. The Office takes reasonable steps to ensure that user data is protected and used solely for its intended purposes.");
        termsList.add("9. Third-Party Sharing and Data Disclosure\n- User information may be shared with third parties for the purpose of evaluating and improving the Office’s services and for comprehensive planning. These third parties are required to maintain the confidentiality of the data they receive and are bound by data protection agreements.");
        termsList.add("10. Governing Law\n- These Terms and Conditions are governed by the laws of the Republic of the Philippines and the University of Makati. Any disputes arising out of the use of this System will be subject to the exclusive jurisdiction of the courts located in Makati City.");
        termsList.add("11. Contact Information\n- For any questions regarding these Terms and Conditions, please contact the UMak Medical and Dental Office through our official social media channels or via email.");

        // Set adapter
        TermsAdapter adapter = new TermsAdapter(termsList);
        recyclerView.setAdapter(adapter);
    }
}