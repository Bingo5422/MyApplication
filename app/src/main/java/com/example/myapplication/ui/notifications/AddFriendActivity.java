package com.example.myapplication.ui.notifications;

import static com.example.myapplication.MainActivity.DomainURL;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.myapplication.Bean.FriendsBean;
import com.example.myapplication.R;
import com.example.myapplication.Utils.CookieJarImpl;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.BufferedSink;

public class AddFriendActivity extends AppCompatActivity {
    private EditText mFriendIdEditText;
    private FriendDatabase mFriendDatabase;

    private ImageView add_fri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friend);
        add_fri = findViewById(R.id.add_fri_back);
        mFriendIdEditText = findViewById(R.id.editTextFriendId);
        mFriendDatabase = Room.databaseBuilder(getApplicationContext(), FriendDatabase.class, "friend_db").build();

        Button addButton = findViewById(R.id.buttonAddFriend);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addFriend();
                finish();
            }
        });

        add_fri.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }


    private void addFriend() {
        String friendId = mFriendIdEditText.getText().toString().trim();
        if (friendId.isEmpty()) {
            Toast.makeText(getApplicationContext(), "请输入好友 ID", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check whether the friend already exists


        // Call the server API to get the friend information, here directly simulated to get the friend's name

        CookieJarImpl cookieJar = new CookieJarImpl(AddFriendActivity.this);
        OkHttpClient client = new OkHttpClient.Builder().cookieJar(cookieJar).build();
        Request request = new Request.Builder()
                .url(DomainURL + "/addfriends/add/id?id=" + friendId)
                .build();
        client.newCall(request).enqueue(new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {
                Log.d(null, "failed to add friend");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {

                    JSONObject res = new JSONObject(response.body().string());
                    String email = res.getString("id");
                    if (email == "0") {

                    } else {
                        FriendsBean friend = new FriendsBean();
                        friend.setEmail(email);
                        friend.setName(res.getString("nickname"));
                        mFriendDatabase.friendDao().addFriend(friend);
                    }
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        });


//        private class AddFriendTask extends AsyncTask<Integer, Void, Boolean> {
//            @Override
//            protected Boolean doInBackground(Integer... integers) {
//                Integer friendId = integers[0];
//
//                // Check whether the friend already exists
//                FriendsBean friend = mFriendDatabase.friendDao().getId(friendId);
//                if (friend != null) {
//                    return false;
//                }
//
//                // Call the server API to get the friend information, here directly simulated to get the friend's name
//
//                CookieJarImpl cookieJar = new CookieJarImpl(AddFriendActivity.this);
//                OkHttpClient client = new OkHttpClient.Builder().cookieJar(cookieJar).build();
//                Request request = new Request.Builder()
//                        .url(DomainURL + "/add/id?id=")
//                        .build();
//                client.newCall(request).enqueue(new Callback() {
//
//                    @Override
//                    public void onFailure(Call call, IOException e) {
//
//                    }
//
//                    @Override
//                    public void onResponse(Call call, Response response) throws IOException {
//                        try {
//                            JSONObject res = new JSONObject(response.body().string());
//                            String email = res.getString("id");
//                            if (email == "0") {
//
//                            } else {
//                                FriendsBean friend = new FriendsBean();
//                                friend.setEmail(email);
//                                friend.setName(res.getString("nickname"));
//                                mFriendDatabase.friendDao().addFriend(friend);
//                            }
//
//                        } catch (JSONException e) {
//                            throw new RuntimeException(e);
//                        }
//                    }
//                });
//
//
//                return true;
//            }
//
//            @Override
//            protected void onPostExecute(Boolean success) {
//                if (success) {
//                    Toast.makeText(getApplicationContext(), "Succeeded in adding a friend", Toast.LENGTH_SHORT).show();
//                    finish();
//                } else {
//                    Toast.makeText(getApplicationContext(), "The friend has been added", Toast.LENGTH_SHORT).show();
//                }
//            }
//        }
    }
}
