package com.example.myapplication.ui.me;

import static com.example.myapplication.ui.me.MeFragment.DomainURL;

import android.content.Context;
import android.os.Looper;
import android.widget.Toast;

import androidx.room.Room;

import com.example.myapplication.Bean.CheckBean;
import com.example.myapplication.Bean.HistoryBean;
import com.example.myapplication.Dao.HistoryDao;
import com.example.myapplication.Dao.RecDataBase;
import com.example.myapplication.Utils.FileUtil;

import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Cookie;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ServHistHttpUtil {

    private RecDataBase recDataBase;
    private HistoryDao historyDao;


}
