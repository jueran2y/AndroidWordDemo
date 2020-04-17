# Android口算项目开发笔记

### 使用dataBinding

使用前需要在build.gradle(Moudel:app) -andriod 添加配置

``` xml
dataBinding.enabled = true
```



<img src="C:\Users\nieru\Pictures\MarkDownPicture\watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3dlaXhpbl80MzczNDA5NQ==,size_16,color_FFFFFF,t_70" alt="pic" style="zoom:50%;" />

***

### 使用ViewModel

需要在build.gradle(Moudel:app)-dependencies 中添加配置

```xml
implementation 'androidx.lifecycle:lifecycle-viewmodel-savedstate:1.0.0-alpha01'
```

![pic](https://img-blog.csdnimg.cn/20190819225105948.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3dlaXhpbl80MzczNDA5NQ==,size_16,color_FFFFFF,t_70)

***
### 创建Fragment

**欢迎界面、问答界面、问答胜利界面、问答失败界面** 这4个页面之间跳转。创建 4 个 Fragment 页面，自动产生了 4 个对应的 xml 文件。

> 1. 为了规范，将所有的文字以**字符串形式**存放在**资源文件中的 strings.xml**
>
> 2. 将字体大小存放到**资源文件中的 dimens.xml** 中

***

### 连接导航文件逻辑图

创建一个 导航文件(Navigation)

![pic](https://img-blog.csdnimg.cn/20190820003107497.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3dlaXhpbl80MzczNDA5NQ==,size_16,color_FFFFFF,t_70)



连接 4 个页面的逻辑图：

**欢迎 ——> 问答 ——> 问答胜利 / 问答失败 ——> 欢迎**

![pic](https://img-blog.csdnimg.cn/20190820003332915.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3dlaXhpbl80MzczNDA5NQ==,size_16,color_FFFFFF,t_70)

在 activity_main.xml 中添加 NavHostFragment，并且选择上面连接的逻辑图

![pic](https://img-blog.csdnimg.cn/20190820003601152.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3dlaXhpbl80MzczNDA5NQ==,size_16,color_FFFFFF,t_70)

界面搭建基本完成
***
### My viewModel

创建一个 **ViewModel** 文件，父类继承 **AndroidViewModel**, 以此来更方便的操控保存的数据。继承后，在 **MyViewModel** 类中，可以直接使用 **getApplication()** 和 **getApplicationContext()** 。因此，就可以在 **MyViewModel** 中直接操纵数据。



​		继承了 AndroidViewModel 后，需要**添加一个构造器**，同时，由于要使用 SavedStateHandle 来永久存储数据，因此我们**在构造器里添加一个 SavedStateHandle 参数来读取数据**。

``` java
public class MyViewModel extends AndroidViewModel {
    private SavedStateHandle handle;
    private static String KEY_HIGH_SCORE = "key_high_score";    // 最高分
    private static String KEY_LEFT_NUMBER = "key_left_number";   // 运算符左边数字
    private static String KEY_RIGHT_NUMBER = "key_right_number";// 运算符右边数字
    private static String KEY_OPERATOR = "key_operator";        // 运算符
    private static String KEY_ANSWER = "key_answer";            // 运算结果
    private static String KEY_CURRENT_SCORE = "key_current_score";  //当前分数
    private static String SAVE_SHP_DATA_NAME = "save_shp_data_name";// SharedPreferences 需要的常量
    boolean win_flag = false;   // 获胜状态，为 true 则当前为获胜，false 则当前为失败

    public MyViewModel(@NonNull Application application, SavedStateHandle handle) {
        super(application);
        // 最高分是需要被永久存储的数据，如果没有存储，说明是第一次运行，则将所有数据初始化
        if(!handle.contains(KEY_HIGH_SCORE)){
            SharedPreferences shp = getApplication().getSharedPreferences(SAVE_SHP_DATA_NAME, Context.MODE_PRIVATE);
            handle.set(KEY_HIGH_SCORE, shp.getInt(KEY_HIGH_SCORE, 0));
            handle.set(KEY_LEFT_NUMBER, 0);
            handle.set(KEY_RIGHT_NUMBER, 0);
            handle.set(KEY_OPERATOR, "+");
            handle.set(KEY_ANSWER, 0);
            handle.set(KEY_CURRENT_SCORE, 0);
        }
        this.handle = handle;
    }

    public MutableLiveData<Integer> getHighScore(){
        return handle.getLiveData(KEY_HIGH_SCORE);
    }
    public MutableLiveData<Integer> getCurrentScore(){
        return handle.getLiveData(KEY_CURRENT_SCORE);
    }
    public MutableLiveData<Integer> getLeftNumber(){
        return handle.getLiveData(KEY_LEFT_NUMBER);
    }
    public MutableLiveData<Integer> getRightNumber(){
        return handle.getLiveData(KEY_RIGHT_NUMBER);
    }
    public MutableLiveData<String> getOperator(){
        return handle.getLiveData(KEY_OPERATOR);
    }
    public MutableLiveData<Integer> getAnswer(){
        return handle.getLiveData(KEY_ANSWER);
    }

    void generator(){ // 生成一道题目
        int LEVEL = 20;
        Random random = new Random();
        int x,y;
        x = random.nextInt(LEVEL) + 1; // x 为 1 到 LEVEL-1 的随机数
        y = random.nextInt(LEVEL) + 1; // y 也为 1 到 LEVEL-1 的随机数
        if(x%2 == 0){
            getOperator().setValue("+"); // x 为偶数则运算符为"+"
            if(x > y){
                getAnswer().setValue(x); // 将较大的数设为答案，则加数与被加数都可以表达出来
                getLeftNumber().setValue(y);
                getRightNumber().setValue(x - y);
            }else{
                getAnswer().setValue(y);
                getLeftNumber().setValue(x);
                getRightNumber().setValue(y - x);
            }
        }else{
            getOperator().setValue("-"); // x 不是偶数则运算符为"-"
            if(x > y){
                getLeftNumber().setValue(x);
                getRightNumber().setValue(y);
                getAnswer().setValue(x - y);
            }else{
                getLeftNumber().setValue(y);
                getRightNumber().setValue(x);
                getAnswer().setValue(y - x);
            }
        }

    }

    void save(){
        SharedPreferences shp = getApplication().getSharedPreferences(SAVE_SHP_DATA_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = shp.edit();
        editor.putInt(KEY_HIGH_SCORE, getHighScore().getValue());
        editor.apply();
    }

    void answerCorrect(){ // 答对问题
        getCurrentScore().setValue(getCurrentScore().getValue() + 1); // 当前分数 +1
        if(getCurrentScore().getValue() > getHighScore().getValue()){ // 如果当前分数比最高分要高
            getHighScore().setValue(getCurrentScore().getValue());  // 将当前分设为最高分
            win_flag = true; // 将状态设置为获胜
        }
        generator(); // 生成一道新题
    }
}
```

### 数据绑定

来到 fragment_title.xml，首先将布局转化为 data binding layout

![pic](https://img-blog.csdnimg.cn/20190820014028740.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3dlaXhpbl80MzczNDA5NQ==,size_16,color_FFFFFF,t_70)

然后在 data 标签中添加变量

``` xml
<data>
    <variable
        name="data"
        type="com.example.caculationtest.MyViewModel" />
</data>
```

将需要绑定的控件进行绑定

``` xml
android:text="@{@string/high_score_message(data.highScore)}"
```

> 注意：dataBinding中会有个警告，如要消除警告，可用 safeUnbox
>
> ``` xml
> android:text="@{String.valueOf(safeUnbox(data.leftNumber))}"
> ```

至此，数据绑定完成

与**数据无关的代码**将**直接在各个页面的 Fragment 中写**，主要包含**页面跳转**，**功能调用**等。

***

### Fragment中的代码

欢迎界面需要**点击按钮进入问答界面**

``` java
public View onCreateView(@NonNull LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        MyViewModel myViewModel;
        //myViewModel = ViewModelProviders.of(requireActivity(), new SavedStateVMFactory(requireActivity())).get(MyViewModel.class);方法过时
     myViewModel = new ViewModelProvider(requireActivity(),new SavedStateViewModelFactory(requireActivity().getApplication(),requireActivity())).get(MyViewModel.class);
        FragmentTitleBinding binding = DataBindingUtil.inflate(inflater, R.layout.fragment_title, container, false); // 获取 binding 对象
        binding.setData(myViewModel);
        binding.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavController controller = Navigation.findNavController(view);	// 获取导航控制器
                controller.navigate(R.id.action_titleFragment_to_questionFragment);	// 通过控制器跳转
            }
        });
        binding.setLifecycleOwner(this);
        return binding.getRoot();
    }
```



问答界面较为复杂，需要**点击按钮，显示数字，并且需要判断输入的数字与答案是否相等，以此来决定跳转失败或是成功界面**。

   ``` java
   public class QuestionFragment extends Fragment {
   
       public QuestionFragment() {
           // Required empty public constructor
       }
   
       @Override
       public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                                Bundle savedInstanceState) {
           final MyViewModel myViewModel;
           //myViewModel = ViewModelProviders.of(requireActivity(), new SavedStateVMFactory(requireActivity())).get(MyViewModel.class);方法过时
        myViewModel = new ViewModelProvider(requireActivity(),new SavedStateViewModelFactory(requireActivity().getApplication(),requireActivity())).get(MyViewModel.class);
           myViewModel.generator(); // 出题
           myViewModel.getCurrentScore().setValue(0); // 重新开始则置零
           final FragmentQuestionBinding binding = DataBindingUtil.inflate(inflater, R.layout.fragment_question, container, false);
           binding.setData(myViewModel);
           binding.setLifecycleOwner(this);
           final StringBuilder builder = new StringBuilder();
           // 按下 数字键 以及 清零键 的事件
           View.OnClickListener listener = new View.OnClickListener() {    
               @Override
               public void onClick(View view) {
                   //按键事件switch实现
                   }
                   if(builder.length() == 0){
                       binding.textView9.setText(getString(R.string.input_indicator));
                   } else {
                      binding.textView9.setText(builder);
                   }
               }
           };
   
           binding.button0.setOnClickListener(listener);
           //...按键绑定
   
   
           binding.buttonSubmit.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View view) {
                   if(Integer.valueOf(builder.toString()).intValue() == myViewModel.getAnswer().getValue()){
                       myViewModel.answerCorrect();
                       builder.setLength(0);
                       binding.textView9.setText(getResources().getString(R.string.answer_correct_message));
                       // builder.append(getResources().getString(R.string.answer_correct_message));
                       }else{
                           NavController controller = Navigation.findNavController(view);
                           if(myViewModel.win_flag) {
                               controller.navigate(R.id.action_questionFragment_to_winFragment);
                               myViewModel.win_flag = false;
                               myViewModel.save();
                           }else{
                           controller.navigate(R.id.action_questionFragment_to_loseFragment);
                       }
   
                   }
               }
           });
           return binding.getRoot();
       }
}
   ```

   问答胜利页面需要**点击按钮，返回欢迎页面**

   ``` java
   public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                                Bundle savedInstanceState) {
       MyViewModel myViewModel;
       //myViewModel = ViewModelProviders.of(requireActivity(), new SavedStateVMFactory(requireActivity())).get(MyViewModel.class);方法过时
        myViewModel = new ViewModelProvider(requireActivity(),new SavedStateViewModelFactory(requireActivity().getApplication(),requireActivity())).get(MyViewModel.class);
       FragmentWinBinding binding = DataBindingUtil.inflate(inflater, R.layout.fragment_win, container, false);
       binding.setData(myViewModel);
       binding.setLifecycleOwner(this);
   
       binding.button11.setOnClickListener(new View.OnClickListener() {
               @Override
           public void onClick(View view) {
               NavController controller = Navigation.findNavController(view);
               controller.navigate(R.id.action_winFragment_to_titleFragment);
           }
       });
   
       return binding.getRoot();
   }
   ```
***
### ActionBar 返回箭头

在软件进入问答界面后，**上方添加一个返回箭头**，点击返回条后跳出提示，选择是否确认，点 OK 则返回欢迎界面，点 Cancel 则取消。

``` java
public class MainActivity extends AppCompatActivity {

    NavController controller;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        controller = Navigation.findNavController(this, R.id.fragment);
        NavigationUI.setupActionBarWithNavController(this, controller); // 界面上方添加一个返回箭头,此时无实际效果
    }

    @Override
    public boolean onSupportNavigateUp() { // 给返回箭头添加功能
        if(controller.getCurrentDestination().getId() == R.id.questionFragment){ // 进入问答界面出现返回箭头
            AlertDialog.Builder builder= new AlertDialog.Builder(this); 
            builder.setTitle(R.string.quit_dialog_to_title);// 返回箭头提示语
            builder.setPositiveButton(R.string.dialog_positive_message, new DialogInterface.OnClickListener() { // 选 OK
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    controller.navigateUp();
                }
            });
            builder.setNegativeButton(R.string.dialog_negative_message, new DialogInterface.OnClickListener() { // 选 Cancel
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                }
            });
            
            AlertDialog dialog = builder.create();
            dialog.show();
        }else if (controller.getCurrentDestination().getId() == R.id.titleFragment) { // 如果是欢迎界面，则退出
            finish();
        }else{ // 除了问答界面按返回会提示，其他界面都会直接回到 欢迎界面，欢迎界面则直接退出
            controller.navigate(R.id.titleFragment); // 回到 欢迎界面
        }
        return super.onSupportNavigateUp();
    }
}
```

***

### 拦截 BACK键

BACK 键默认功能是返回上一步，我们可以拦截 BACK 键，修改它的功能

``` java
public void onBackPressed() { // 按下 BACK 键时的操作
        onSupportNavigateUp(); // 直接调用上面写好的返回箭头的功能
}
```

***

### 横屏适配

很多软件竖屏使用时是正常的，但是屏幕旋转后，界面便会变的很奇怪。要么
**设置屏幕不可旋转**

``` xml
<activity android:name=".MainActivity"
            android:screenOrientation="portrait">
```

要么对**软件进行横屏适配**，即，**将所有页面再创建一个横屏的版本**。

***

### 总结

ViewModel类 专门用来管理变量，将变量管理与软件布局分离，在变量多的时候十分方便。

使用 JetPack 无需利用 savedInstanceState 来临时保存数据，自动完成数据的存储。

Data Binding 数据绑定可以在 xml 文件中动态显示数据，或是调用与数据相关的方法，并且可以通过 binding 对象来直接获取组件成员，无需再通过 findViewById() 方法，使得代码十分精简，更加直观。

通过让 MyViewModel 继承 AndroidViewModel，更方便的操控保存的数据。
继承后，在 MyViewModel类中，可以直接 getApplication() 和 getApplicationContext()。因此，就可以在 MyViewModel 中直接操纵数据。





# Android studio3.6新特性

### viewBinding

由于之前很多控件都需要频繁的findViewById，显得很啰嗦，所以加入了这个特性

使用前现在buildapp里面添加启用代码

```java
viewBinding{
    enabled true
}
```

##### 使用方式

​	创建对象

```java
private ActivityMainBinding binding;//binding名字是每个界面的名字自动生成的
```

​	实例化

```java
binding = ActivityMainBinding.inflate(getLayoutInflater());
setContentView(binding.getRoot());//代替原来的setContentView方法
```

​	然后就可以愉快的直接使用控件了

```java
binding.buttonInsert.setOnClickListener...
```



### viewBinding在RecyclerView中的使用（Adapter）

```java
@NonNull
    @Override  //这是RecyclerView创建呼叫的函数
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        CellcardBinding binding = CellcardBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);//代替this
        return new MyViewHolder(binding);
    }


@Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {
        //把要请求的单词号给到word
        Word word = allWords.get(position);
        //关联数据
        holder.binding.textViewNumber.setText(String.valueOf(position + 1));
        holder.binding.textViewEnglish.setText(word.getWord());
        holder.binding.textViewChinese.setText(word.getChineseMeaning());
    }


//自定义Holder绑定自己定义的组件里面的控件
    static class MyViewHolder extends RecyclerView.ViewHolder{
        CellcardBinding binding;
        public MyViewHolder(@NonNull CellcardBinding one) {
            super(one.getRoot());
            binding = one;
        }
    }
```





# Android Room使用

### 前言

Room是安卓的数据库 基于SQLite

***

### 使用前

使用Room之前要导入库，在**dependencies**里面添加依赖

```java
def room_version = "2.2.3"//必加

      implementation "androidx.room:room-runtime:$room_version"//必加
      annotationProcessor "androidx.room:room-compiler:$room_version" // For Kotlin use kapt instead of annotationProcessor//必加

      // optional - Kotlin Extensions and Coroutines support for Room Kotlin可选
      implementation "androidx.room:room-ktx:$room_version"

      // optional - RxJava support for Room Rxjava可选
      implementation "androidx.room:room-rxjava2:$room_version"

      // optional - Guava support for Room, including Optional and ListenableFuture 可选
      implementation "androidx.room:room-guava:$room_version"

      // Test helpers
      testImplementation "androidx.room:room-testing:$room_version"//必加
```



### Room项目实例1

##### 创建Word类

```java
package com.example.demo005;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity //声明
public class Word {
    @PrimaryKey(autoGenerate = true)
    private int id;
    
    @ColumnInfo(name = "english_word")
    private String word;
    
    @ColumnInfo(name = "chinese_meaning")
    private String chineseMeaning;

    public Word(String word, String chineseMeaning) {
        this.word = word;
        this.chineseMeaning = chineseMeaning;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public String getChineseMeaning() {
        return chineseMeaning;
    }

    public void setChineseMeaning(String chineseMeaning) {
        this.chineseMeaning = chineseMeaning;
    }
}

```



##### 创建接口类WordDao

```java
package com.example.demo005;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao //Database access object
public interface WordDao {
    @Insert
    void insertWords(Word... words);

    @Update
    void updateWords(Word... words);

    @Delete
    void deleteWords(Word... words);

    @Query("DELETE FROM WORD")
    void deleteAllWords();

    @Query("SELECT * FROM WORD ORDER BY ID DESC")
    List<Word> getAllWords();
}
```



 ##### 创建抽象类WordDatabase

```java
package com.example.demo005;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {Word.class},version = 1,exportSchema = false)
public abstract class WordDatabase extends RoomDatabase {
    //主要是用来给WordDao初始化
    public abstract WordDao getWordDao();
}
```



##### MainActivity实例化

```java
WordDatabase wordDatabase;
WordDao wordDao;
wordDatabase = Room.databaseBuilder(this,WordDatabase.class,"word_database")
                .allowMainThreadQueries()//原本Dao是不允许主线程使用的，这里进行强制化使用（不推荐）
                .build();
wordDao = wordDatabase.getWordDao();
```



##### 创建一个数据刷新方法

```java
void updateView(){
        List<Word> list = wordDao.getAllWords();
        String text = "";
        for (int i = 0;i < list.size();i++){
            Word word = list.get(i);
            text += word.getId() + " : " + word.getWord() + " = " + word.getChineseMeaning() + "\r\n";
        }
        textView.setText(text);
}
```



##### Word的增删改查

``` java
//Word增加单词
Word word2 = new Word("World","世界");
wordDao.insertWords(word1,word2);
updateView();

//Word清空数据
wordDao.deleteAllWords();
updateView();

//Word删除单词
Word word = new Word("Hello","你好");
word.setId(21);
wordDao.deleteWords(word);
updateView();

//Word修改单词
Word word = new Word("Apple","苹果");
word.setId(20);
wordDao.updateWords(word);
updateView();
```



##### 项目界面

<img src="C:\Users\nieru\Pictures\MarkDownPicture\批注 2020-04-09 171611.png" alt="pic" style="zoom:50%;" />



### Room项目实例1 改进

##### 更改数据刷新方式

由于每一次都要手动刷新数据很麻烦，我们可以使用LiveData来更新数据

WordDao类里面更改

```java
@Query("SELECT * FROM WORD ORDER BY ID DESC")
    //List<Word> getAllWords();之前返回值是List，我们可以更改为LiveData
	LiveData<List<Word>> getAllWords();
```

MainActivity类里面更改

```java
/**
	之前的updateView方法可以删除
	void updateView(){}
*/
//初始化LiveData
LiveData<List<Word>> list;
//实例化对象，并进行观察
wordDao = wordDatabase.getWordDao();
        list = wordDao.getAllWords();
        list.observe(this, new Observer<List<Word>>() {
            @Override
            public void onChanged(List<Word> words) {
                String text = "";
                for (int i = 0;i < words.size();i++){
                    Word word = words.get(i);
                    text += word.getId() + " : " + word.getWord() + " = " + word.getChineseMeaning() + "\r\n";
                }
                textView.setText(text);
            }
        });
```



##### 更改WordDataBase为单例模式

因为DataBase的创建很耗资源，所以我们不希望它重复创建，当他需要的时候我们可以返回同一个实例。

我们对WordDataBase类进行修改

```java
//Singleton单例模式
@Database(entities = {Word.class},version = 1,exportSchema = false)
public abstract class WordDatabase extends RoomDatabase {
    private static WordDatabase INSTANCE;

    //synchronized锁 多线程请求进行排队处理
    static synchronized WordDatabase getDatabase(Context context){
        if(INSTANCE == null){
            INSTANCE = Room.databaseBuilder(context.getApplicationContext(),WordDatabase.class,"word_database")
                    .build();
        }
        return INSTANCE;
    }
    public abstract WordDao getWordDao();
}
```



##### 把数据的修改变成辅线程

在MainActivity中创建静态内部类（当然自己外部类实现也可以）//或在ViewModel里修改

```java
//增加单词
    static class InsterAsyncTask extends AsyncTask<Word,Void,Void>{
        private WordDao wordDao;

        public InsterAsyncTask(WordDao wordDao) {
            this.wordDao = wordDao;
        }

        @Override
        protected Void doInBackground(Word... words) {
            wordDao.insertWords(words);
            return null;
        }
    }

    //清空单词
    static class DeleteAllAsyncTask extends AsyncTask<Void,Void,Void>{
        private WordDao wordDao;

        public DeleteAllAsyncTask(WordDao wordDao) {
            this.wordDao = wordDao;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            wordDao.deleteAllWords();
            return null;
        }
    }

    //删除单词
    static class DeleteAsyncTask extends AsyncTask<Word,Void,Void>{
        private WordDao wordDao;

        public DeleteAsyncTask(WordDao wordDao) {
            this.wordDao = wordDao;
        }

        @Override
        protected Void doInBackground(Word... words) {
            wordDao.deleteWords(words);
            return null;
        }
    }

    //修改单词
    static class UpDateAsyncTask extends AsyncTask<Word,Void,Void>{
        private WordDao wordDao;

        public UpDateAsyncTask(WordDao wordDao) {
            this.wordDao = wordDao;
        }

        @Override
        protected Void doInBackground(Word... words) {
            wordDao.updateWords(words);
            return null;
        }
    }
```



然后把之前的实现方式进行更改

```java
//Word增加单词
Word word2 = new Word("World","世界");
new InsterAsyncTask(wordDao).execute(word1,word2);

//Word清空数据
new DeleteAllAsyncTask(wordDao).execute();

//Word删除单词
Word word = new Word("Hello","你好");
word.setId(21);
new DeleteAsyncTask(wordDao).execute(word);

//Word修改单词
Word word = new Word("Apple","苹果");
word.setId(20);
new UpDateAsyncTask(wordDao).execute(word);
```



##### 减少MainActivity的臃肿

MainActivity应该只管理界面，数据应该创建一个新的类来管理

创建WordViewModel

```java
public class WordViewModel extends AndroidViewModel {

    private WordRepository wordRepository;//创建的仓库类

    public WordViewModel(@NonNull Application application) {
        super(application);
        wordRepository = new WordRepository(application);
    }

    public LiveData<List<Word>> getAllWordList() {
        return wordRepository.getAllWordList();
    }

    void insterWord(Word... words){
        wordRepository.insterWord(words);
    }
    void upDateWord(Word... words){
        wordRepository.upDateWord(words);
    }
    void DeleteAllWord(){
        wordRepository.DeleteAllWord();
    }
    void DeleteWord(Word... words){
        wordRepository.DeleteWord(words);
    }

}
```



MainActivity修改

```java
WordViewModel wordViewModel;//创建对象
@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        wordViewModel = new ViewModelProvider(this,new ViewModelProvider.AndroidViewModelFactory(getApplication())).get(WordViewModel.class);

        wordViewModel.getAllWordList().observe(this, new Observer<List<Word>>() {
            @Override
            public void onChanged(List<Word> words) {
                StringBuilder text = new StringBuilder();
                for (int i = 0;i < words.size();i++){
                    Word word = words.get(i);
                    text.append(word.getId()).append(" : ").append(word.getWord()).append(" = ").append(word.getChineseMeaning()).append("\r\n");
                }
                textView.setText(text.toString());
            }
        });
        
        ...省略
    }
```



##### 存储数据应该建一个仓库类

创建WordRepository类

```java
//管理数据的仓库类
public class WordRepository {
    private WordDao wordDao;
    private LiveData<List<Word>> allWordList;

    public WordRepository(Context context) {
        WordDatabase wordDatabase = WordDatabase.getDatabase(context.getApplicationContext());
        wordDao = wordDatabase.getWordDao();
        allWordList = wordDao.getAllWords();
    }

    public LiveData<List<Word>> getAllWordList() {
        return allWordList;
    }


    void insterWord(Word... words){
        new InsterAsyncTask(wordDao).execute(words);
    }
    void upDateWord(Word... words){
        new UpDateAsyncTask(wordDao).execute(words);
    }
    void DeleteAllWord(){
        new DeleteAllAsyncTask(wordDao).execute();
    }
    void DeleteWord(Word... words){
        new DeleteAsyncTask(wordDao).execute(words);
    }

    //增加单词
    static class InsterAsyncTask extends AsyncTask<Word,Void,Void> {
        private WordDao wordDao;

        public InsterAsyncTask(WordDao wordDao) {
            this.wordDao = wordDao;
        }

        @Override
        protected Void doInBackground(Word... words) {
            wordDao.insertWords(words);
            return null;
        }
    }

    //清空单词
    static class DeleteAllAsyncTask extends AsyncTask<Void,Void,Void>{
        private WordDao wordDao;

        public DeleteAllAsyncTask(WordDao wordDao) {
            this.wordDao = wordDao;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            wordDao.deleteAllWords();
            return null;
        }
    }

    //删除单词
    static class DeleteAsyncTask extends AsyncTask<Word,Void,Void>{
        private WordDao wordDao;

        public DeleteAsyncTask(WordDao wordDao) {
            this.wordDao = wordDao;
        }

        @Override
        protected Void doInBackground(Word... words) {
            wordDao.deleteWords(words);
            return null;
        }
    }

    //修改单词
    static class UpDateAsyncTask extends AsyncTask<Word,Void,Void>{
        private WordDao wordDao;

        public UpDateAsyncTask(WordDao wordDao) {
            this.wordDao = wordDao;
        }

        @Override
        protected Void doInBackground(Word... words) {
            wordDao.updateWords(words);
            return null;
        }
    }
}
```



### Room项目实例补充（RecyclerView的使用）

为了更好也更节省空间的展示内容我们使用可回收机制的RecyclerView视图，当数据滚动到屏幕之外系统将自动回收，节省性能。

使用RecyclerView之前除了要创建视图，还需要创建组件视图也就是在RecyclerView里面一栏一栏当中的内容，还需要创建RecyclerView的适配器Adapter用来管理RecyclerView里面的数据。



##### 组件绘制

<img src="C:\Users\nieru\Pictures\MarkDownPicture\批注 2020-04-08 162752.png" alt="pic" style="zoom:60%;" />





##### 创建适配器

```java
public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {
    List<Word> allWords = new ArrayList<>();

    //用来判断是否要用卡片视图
    Boolean isUseCard;

    public MyAdapter(Boolean isUseCard) {
        this.isUseCard = isUseCard;
    }

    public void setAllWords(List<Word> allWords) {
        this.allWords = allWords;
    }

    @NonNull
    @Override  //这是RecyclerView创建呼叫的函数
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View itemView;
        if(isUseCard){
            itemView = layoutInflater.inflate(R.layout.cellcard,parent,false);
        }else{
            itemView = layoutInflater.inflate(R.layout.cellview,parent,false);
        }
        //把组件界面给传递过去
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        //把要请求的单词号给到word
        Word word = allWords.get(position);
        //关联数据
        holder.textViewNumber.setText(String.valueOf(position + 1));
        holder.textViewEnglish.setText(word.getWord());
        holder.textViewChinese.setText(word.getChineseMeaning());
    }

    @Override
    public int getItemCount() {
        return allWords.size();
    }
  
    //自定义Holder绑定自己定义的组件里面的控件
    static class MyViewHolder extends RecyclerView.ViewHolder{
        TextView textViewNumber,textViewEnglish,textViewChinese;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewNumber = itemView.findViewById(R.id.textViewNumber);
            textViewChinese = itemView.findViewById(R.id.textViewChinese);
            textViewEnglish = itemView.findViewById(R.id.textViewEnglish);
        }
    }
}
```



##### MainAcitvity实例化RecyclerView

```java
RecyclerView recyclerView;
MyAdapter myAdapter1,myAdapter2;

myAdapter1 = new MyAdapter(false);
myAdapter2 = new MyAdapter(true);

//设置RecyclerView的显示方式是线性一行行还是GridLayoutManager有行列
recyclerView.setLayoutManager(new LinearLayoutManager(this));
recyclerView.setAdapter(myAdapter1);
recyclerView.setAdapter(myAdapter2);

myAdapter1.setAllWords(words);
//刷新视图
myAdapter1.notifyDataSetChanged();
myAdapter2.setAllWords(words);
//刷新视图
myAdapter2.notifyDataSetChanged();

```



##### 修改之后的程序（加入了视图切换功能和查询在线词典功能）

###### 适配器代码

```java
public class MainActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    MyAdapter myAdapter1,myAdapter2;
    Button buttonInsert,buttonClear;
    Switch isUseCard;

    WordViewModel wordViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        isUseCard = findViewById(R.id.switchcard);

        recyclerView = findViewById(R.id.recyclerView);
        myAdapter1 = new MyAdapter(false);
        myAdapter2 = new MyAdapter(true);
        //设置RecyclerView的显示方式是线性一行行还是GridLayoutManager有行列
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(myAdapter1);


        wordViewModel = new ViewModelProvider(this,new ViewModelProvider.AndroidViewModelFactory(getApplication())).get(WordViewModel.class);

        wordViewModel.getAllWordList().observe(this, new Observer<List<Word>>() {
            @Override
            public void onChanged(List<Word> words) {
                myAdapter1.setAllWords(words);
                //刷新视图
                myAdapter1.notifyDataSetChanged();
                myAdapter2.setAllWords(words);
                //刷新视图
                myAdapter2.notifyDataSetChanged();
            }
        });

        buttonInsert = findViewById(R.id.buttonInsert);
        buttonClear = findViewById(R.id.buttonClear);



        isUseCard.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    //设置RecyclerView的数据适配器
                    recyclerView.setAdapter(myAdapter2);
                }else{
                    //设置RecyclerView的数据适配器
                    recyclerView.setAdapter(myAdapter1);
                }
            }
        });

        buttonInsert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Word word1 = new Word("Hello","你好");
                Word word2 = new Word("World","世界");
                wordViewModel.insterWord(word1,word2);
            }
        });

        buttonClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wordViewModel.DeleteAllWord();
            }
        });
    }
}
```



###### MainActivity代码

```java
public class MainActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    MyAdapter myAdapter1,myAdapter2;
    Button buttonInsert,buttonClear;
    Switch isUseCard;

    WordViewModel wordViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        isUseCard = findViewById(R.id.switchcard);

        recyclerView = findViewById(R.id.recyclerView);
        myAdapter1 = new MyAdapter(false);
        myAdapter2 = new MyAdapter(true);
        //设置RecyclerView的显示方式是线性一行行还是GridLayoutManager有行列
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(myAdapter1);


        wordViewModel = new ViewModelProvider(this,new ViewModelProvider.AndroidViewModelFactory(getApplication())).get(WordViewModel.class);

        wordViewModel.getAllWordList().observe(this, new Observer<List<Word>>() {
            @Override
            public void onChanged(List<Word> words) {
                myAdapter1.setAllWords(words);
                //刷新视图
                myAdapter1.notifyDataSetChanged();
                myAdapter2.setAllWords(words);
                //刷新视图
                myAdapter2.notifyDataSetChanged();
            }
        });

        buttonInsert = findViewById(R.id.buttonInsert);
        buttonClear = findViewById(R.id.buttonClear);



        isUseCard.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    //设置RecyclerView的数据适配器
                    recyclerView.setAdapter(myAdapter2);
                }else{
                    //设置RecyclerView的数据适配器
                    recyclerView.setAdapter(myAdapter1);
                }
            }
        });

        buttonInsert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Word word1 = new Word("Hello","你好");
                Word word2 = new Word("World","世界");
                wordViewModel.insterWord(word1,word2);
            }
        });

        buttonClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wordViewModel.DeleteAllWord();
            }
        });
    }
}
```



### 数据库版本迁移

如果要在word中设置新的列，每一次更新都需要变更database里的Version。

##### 迁移策略

1. 破坏性重建

   ```java
   //synchronized锁 多线程请求进行排队处理
       static synchronized WordDatabase getDatabase(Context context){
           if(INSTANCE == null){
               INSTANCE = Room.databaseBuilder(context.getApplicationContext(),WordDatabase.class,"word_database")
                       .fallbackToDestructiveMigration()//破坏性重建
                       .build();
           }
           return INSTANCE;
    }
   ```

2. 非破坏性重建

   ```java
   //synchronized锁 多线程请求进行排队处理
       static synchronized WordDatabase getDatabase(Context context){
           if(INSTANCE == null){
               INSTANCE = Room.databaseBuilder(context.getApplicationContext(),WordDatabase.class,"word_database")
                       .addMigrations(MIGRATION2_3)
                       .build();
           }
           return INSTANCE;
       }
   
       static final Migration MIGRATION2_3 = new Migration(2,3) {
           @Override
           public void migrate(@NonNull SupportSQLiteDatabase database) {
               //                修改       表名字   操作      列名字    类型       缺省值1
               database.execSQL("ALTER TABLE word ADD COLUMN foo_data INTEGER NOT NULL DEFAULT 1");
           }
       };
   ```



由于SQLite没有删除某一列的功能，所以要想删除某一列就得 创建表 迁移表 删旧表 改名字

```java
//synchronized锁 多线程请求进行排队处理
    static synchronized WordDatabase getDatabase(Context context){
        if(INSTANCE == null){
            INSTANCE = Room.databaseBuilder(context.getApplicationContext(),WordDatabase.class,"word_database")
                    .addMigrations(MIGRATION3_4)
                    .build();
        }
        return INSTANCE;
    }

    //删除某一列
    static final Migration MIGRATION3_4 = new Migration(3,4) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            //创建新表
            database.execSQL("CREATE TABLE word_temp (id INTEGER PRIMARY KEY NOT NULL, english_word TEXT," +
                    "chinese_meaning TEXT)");
            //迁移表
            database.execSQL("INSERT INTO word_temp (id,english_word,chinese_meaning)" +
                    "SELECT id,english_word,chinese_meaning FROM word");
            //删除旧表
            database.execSQL("DROP TABLE word");
            //更改新表名字为旧表
            database.execSQL("ALTER TABLE word_temp RENAME TO word");
        }
    };
```



### Room项目实例补充（添加是否显示中英文按钮）

##### 组件绘制

<img src="C:\Users\nieru\Pictures\MarkDownPicture\组件绘制.png" alt="pic" style="zoom:60%;" />

##### Word中添加列

添加一列布尔值用来判断是否显示中文

```java
@ColumnInfo(name = "isChineseInvisble")
private boolean isChineseInvisable;

public boolean isChineseInvisable() {
        return isChineseInvisable;
}
public void setChineseInvisable(boolean chineseInvisable) {
        isChineseInvisable = chineseInvisable;
}
```



##### 修改MyAdapter代码

```java
public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {
    List<Word> allWords = new ArrayList<>();
    WordViewModel wordViewModel;//用来更新word里面的数据

    //用来判断是否要用卡片视图
    Boolean isUseCard;

    public MyAdapter(Boolean isUseCard,WordViewModel wordViewModel) {
        this.isUseCard = isUseCard;
        this.wordViewModel = wordViewModel;
    }

    public void setAllWords(List<Word> allWords) {
        this.allWords = allWords;
    }

    @NonNull
    @Override  //这是RecyclerView创建呼叫的函数
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        CellcardBinding binding = CellcardBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);//代替this
        CellviewBinding bindingView = CellviewBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);
        if(isUseCard){
            return new MyViewHolder(binding);
        }else {
            return new MyViewHolder(bindingView);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {
        //把要请求的单词号给到word
        final Word word = allWords.get(position);
        if (isUseCard){
            //关联数据
            holder.binding.textViewNumber.setText(String.valueOf(position + 1));
            holder.binding.textViewEnglish.setText(word.getWord());
            holder.binding.textViewChinese.setText(word.getChineseMeaning());
            //防止Bug RecyclerView回收再利用会对之前的Switch产生影响
            holder.binding.switchChineseInvisble.setOnCheckedChangeListener(null);
            if(word.isChineseInvisable()){//判断word里面的布尔值是否显示中文
                holder.binding.switchChineseInvisble.setChecked(true);//设置switch保持住true
                holder.binding.textViewChinese.setVisibility(View.INVISIBLE);
            }else{
                holder.binding.switchChineseInvisble.setChecked(false);
                holder.binding.textViewChinese.setVisibility(View.VISIBLE);
            }
        }else{
            //关联数据
            holder.bindingView.textViewNumber.setText(String.valueOf(position + 1));
            holder.bindingView.textViewEnglish.setText(word.getWord());
            holder.bindingView.textViewChinese.setText(word.getChineseMeaning());
            holder.bindingView.switchChineseInvisble.setOnCheckedChangeListener(null);
            if(word.isChineseInvisable()){
                holder.bindingView.switchChineseInvisble.setChecked(true);
                holder.bindingView.textViewChinese.setVisibility(View.INVISIBLE);
            }else{
                holder.bindingView.switchChineseInvisble.setChecked(false);
                holder.bindingView.textViewChinese.setVisibility(View.VISIBLE);
            }
        }
        //监听Switch的状态
        holder.bindingView.switchChineseInvisble.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    word.setChineseInvisable(true);//设置Word里面的布尔值状态
                    holder.bindingView.textViewChinese.setVisibility(View.INVISIBLE);//文本不可见（位置不变） GONE（不可见位置改变）
                    wordViewModel.upDateWord(word);
                }else{
                    word.setChineseInvisable(false);
                    holder.bindingView.textViewChinese.setVisibility(View.VISIBLE);
                    wordViewModel.upDateWord(word);
                }
            }
        });
        holder.bindingView.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse("https://fanyi.baidu.com/?aldtype=16047#en/zh/"+holder.bindingView.textViewEnglish.getText());
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(uri);
                holder.itemView.getContext().startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return allWords.size();
    }

    //自定义Holder绑定自己定义的组件里面的控件
    static class MyViewHolder extends RecyclerView.ViewHolder{
        CellcardBinding binding;
        CellviewBinding bindingView;
        public MyViewHolder(@NonNull CellcardBinding one) {
            super(one.getRoot());
            binding = one;
        }
        public MyViewHolder(@NonNull CellviewBinding one) {
            super(one.getRoot());
            bindingView = one;
        }
    }
}
```



# Android小技巧

### 不让键盘弹起时打乱你的布局

在项目的AndroidManifest.xml文件中界面对应的<activity>里加入android:windowSoftInputMode="stateVisible|adjustResize"，这样会让屏幕整体上移。如果加上的是
        android:windowSoftInputMode="adjustPan"这样键盘就会覆盖屏幕。



### 弹出和关闭软键盘

```java
/**
     * 显示键盘
     *
     * @param et 输入焦点
     */
    public void showInput(final EditText et) {
        et.requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        imm.showSoftInput(et, InputMethodManager.SHOW_IMPLICIT);
    }
 
    /**
     * 隐藏键盘
     */
    protected void hideInput() {
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        View v = getWindow().peekDecorView();
        if (null != v) {
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        }
    }
```

另外，避免软键盘弹出会覆盖底部控件的方法是在布局文件根布局加上一个属性：

```java
android:fitsSystemWindows="true"
```

设置默认不弹出键盘：

```java
getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE | WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);// 设置默认键盘不弹出
```



