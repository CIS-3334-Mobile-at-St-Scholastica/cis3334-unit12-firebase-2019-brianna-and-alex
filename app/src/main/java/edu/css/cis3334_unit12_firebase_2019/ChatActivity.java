package edu.css.cis3334_unit12_firebase_2019;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class ChatActivity extends AppCompatActivity {

    Button btnPost;
    Button btnLogout;
    EditText etMessage;
    TextView tvMsgList;


    /**
     * This method is called when the activity is first created
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        btnPost = findViewById(R.id.buttonPost);
        btnLogout = findViewById(R.id.buttonChatLogout);
        etMessage = findViewById(R.id.editTextMessage);
        tvMsgList = findViewById(R.id.textViewMsgList);

        //Gets the login and password that user entered and clears it for the further entrees.
        btnPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msgText = etMessage.getText().toString();
                etMessage.setText("");           // clear out the message text box to be ready for the next message


                // This does not push message to firebase yet...
                //    The next line should really be in the Firebase onDataChange() method
                tvMsgList.setText(msgText+ "\n" + tvMsgList.getText());

            }
        });

        // Logs out the user
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();           // quit this activity and return to mainActivity
            }
        });

    }
}
