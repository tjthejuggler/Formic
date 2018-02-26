package com.example.malabarista.formic;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.os.PowerManager;
import android.os.Vibrator;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.MultiAutoCompleteTextView;
import android.widget.NumberPicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.lang.reflect.Array;
import java.nio.channels.FileChannel;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Random;


import static com.example.malabarista.formic.R.id.addModifiersHeader;
import static com.example.malabarista.formic.R.id.end;
//import static com.example.malabarista.formic.R.id.histRadButPattern;
import static com.example.malabarista.formic.R.id.numberOfObjectsSpinner;
import static com.example.malabarista.formic.R.id.objectTypeSpinner;
import static com.example.malabarista.formic.R.id.randSetWithHist;
import static com.example.malabarista.formic.R.id.runRadButEndurance;
//import static com.example.malabarista.formic.R.id.histRadButEndurance;
//import static com.example.malabarista.formic.R.id.histRadButBoth;
//import static com.example.malabarista.formic.R.id.settingsMaxNumObjEditText;
import static com.example.malabarista.formic.R.id.tabHost;
import static java.lang.Math.min;

public class MainActivity extends AppCompatActivity {

    MediaPlayer drillCompleteSound;
    MediaPlayer enduranceIntentionalEndPersonalBestBrokenSound;
    MediaPlayer enduranceUnintentionalEndPersonalBestBrokenSound;

    DatabaseHelper myDb; //used by anything that is going to use our database

    AutoCompleteTextView patternsATV; //the unintentionalEnddown menu that holds our Modifiers
    List<String> modifiers = new ArrayList<>(); //our list of Modifiers

    MultiAutoCompleteTextView modifierMATV; //the unintentionalEnddown menu that holds our patterns
    List<String> patterns = new ArrayList<>(); //our list of patterns

    List<String> listOfModifiersWithPastWithSelectedPattern = new ArrayList<>();//our list of modifier combinations that have a past with
    // the currently selected entry(pattern/num of objs/obj type) combo

    Button mainModifiersWithPastButton;

    //used to seperate items, it is just a group of characters that are unlikely to be used by a user
    String buffer = "@@##&&";

    TextView historyPatternNameTextView;
    // TextView addPatternNameTextView;

    Spinner numOfObjsSpinner;
    List<String> addPatNumberOfObjectsForSpinner = new ArrayList<>();//{"1","2","3","4","5","6","7","8","9","10","11"};

    Spinner objTypeSpinner;
    //List<String> addPatObjTypeForSpinner = new ArrayList<String>();//{"Balls","Beanbags","Clubs","Rings"};

    //used for importing and exporting the db, needs to match the dbName in DatabaseHelper
    String dbName = "formic.db";

    String patternAddTabCurrentlyUpdatedWith = "";

    //--------------------------BEGIN RUN TAB VARIABLES----------------------------------
    TextView runEntryName;
    Button runTimerBeginButton;
    Boolean currentlyInEndurRun = false;
    Boolean currentlyInDrillSet = false;
    Boolean currentlyInBetweenDrillSets = false;
    Boolean drillHeadphoneClicked = false;

    RadioGroup runRadioGroup;
    RadioButton runRadioButtonEndurance;
    RadioButton runRadioButtonDrill;

    TextView runsNumberOfSetsLabel;
    TextView runColonLabel2;
    TextView runColonLabel1;

    NumberPicker runNumberPickerHours;
    NumberPicker runNumberPickerMinutes;
    NumberPicker runNumberPickerSeconds;
    NumberPicker runNumberPickerSets;

    CheckBox runDrillRestartSetCheckBox;
    CheckBox runHeadphoneCheckBox;
    int runDrillAttemptCounter;
    int runDrillTimeSpentOnFailedAttempts;
    int timeSpentOnThisAttempt;

    CountDownTimer runsEnduranceTimer;
    int runsEnduranceTimerCurrentTime;

    CountDownTimer runsDrillTimer;
    int runNumberOfSetsCompleted;

    //used for the time buffer that takes place before each drill/endurance attempt
    boolean inBuffer;
    int totalBufferSeconds = 4;
    int currentBufferSeconds = 0;

    boolean drillCompleted = false;

    String runCurrentDateAndTimeOfMostRecentRunBegin;

    TextView runSessionDateAndTimeTextView;
    Button runSessionNoteButton;
    Button runSessionCloseButton;

//--------------------------END RUN TAB VARIABLES----------------------------------

    //--------------------------BEGIN ADD TAB PATTERN VARIABLES----------------------------------
    Button addDeletePatternButton;
    Button addAddPatternButton;

    Button addBulkSiteswapButton;

    TextView addPatternEntryName;

    EditText addPatternDescriptionEditText;

    EditText addPatternSiteswapEditText;

    List<String> listOfReasonsForIntentionalEnd = new ArrayList<>();

    List<String> listOfThoughtsBeforeUnintentionalEnd = new ArrayList<>();

    TextView addPatLinkTextView;
    EditText addPatternLinkEditText;
    Button addLinkGoButton;

//--------------------------END ADD TAB PATTERN VARIABLES----------------------------------

    //--------------------------BEGIN ADD TAB MODIFIERS VARIABLES----------------------------------
    Button addModifierDeleteBut;
    Button addModifierNewBut;

    Spinner addModifierNamesSpinner;
    List<String> addModifierNamesForSpinner = new ArrayList<>(); //our list of patterns

    // String[] addModifierNamesForSpinner = {"tempA","tempB","tempC"};

    EditText addModifierDescriptionEditText;
//--------------------------END ADD TAB MODIFIERS VARIABLES----------------------------------

//--------------------------BEGIN HISTORY TAB VARIABLES----------------------------------

    TextView histFilterExpandCollapseTextView;
    CheckBox histMainInputsCheckbox;
    CheckBox histObjectTypesOtherThanCurrentCheckbox;
    CheckBox histNumberOfObjectsOtherThanCurrentCheckbox;
    CheckBox histPatternNamesOtherThanCurrentCheckbox;
    CheckBox histModifierCombinationOtherThanExactlyCurrentCheckbox;
    CheckBox histModifierCombinationThatDoesntContainCurrentCheckbox;
    CheckBox histRunsNotInThisSessionCheckbox;
    Spinner histSessionDateAndTimesSpinner;
    CheckBox histEnduranceCheckbox;
    CheckBox histIntentionalEndCheckbox;
    CheckBox histUnintentionalEndCheckbox;
    CheckBox histEndurancePersonalBestCheckbox;
    CheckBox histEnduranceNotPersonalBestCheckbox;
    CheckBox histDrillCheckbox;
    CheckBox histRestartSetCheckbox;
    CheckBox histRestartSetDroplessCheckbox;
    CheckBox histRestartSetNotDroplessCheckbox;
    CheckBox histDontRestartCheckbox;
    CheckBox histInitialRunCheckbox;
    CheckBox histNonInitialRunCheckbox;


    List<String> listOfSessionDateAndTimes = new ArrayList<>();
    //TextView historySessionRecordsTextView;
    Button historySessionNotesButton;


    // RadioGroup historyPatternTypeRadioGroup;
    RadioButton historyRadioButtonEndurance;
    RadioButton historyRadioButtonDrill;


    // RadioGroup historyIntentionalEndUnintentionalEndBothRadioGroup;
    RadioButton historyRadioButtonIntentionalEnd;
    RadioButton historyRadioButtonUnintentionalEnd;
    RadioButton historyRadioButtonBoth;

    Button historyEditButton;

    TextView historyRecordsTextView;

    CheckBox historyDrillRestartSetOnUnintentionalEnds;

    int currentIntentionalEndPersonalBest;
    int currentUnintentionalEndPersonalBest;
    int personalBestIntentionalEnd = 0;
    int personalBestUnintentionalEnd = 0;
    boolean personalBestIntentionalEndAudioHasPlayed = false;
    boolean personalBestUnintentionalEndAudioHasPlayed = false;

//--------------------------END HISTORY TAB VARIABLES----------------------------------

//--------------------------BEGIN SETTINGS TAB VARIABLES----------------------------------

    EditText settingsMaxNumberOfObjectsEditText;

    Button settingsObjectTypeAddButton;
    Button settingsObjectTypeRemoveButton;


    List<String> addPatObjTypeForSpinner = new ArrayList<>(); //our list of Object Types
    List<String> addPatObjTypeForMATV = new ArrayList<>(); //our list of patterns

    CheckBox settingsRunDrillAudioCheckBox;

    CheckBox settingsKeepScreenOnDuringRuns;

    EditText settingsBufferSecondsEditText;

    CheckBox settingsRunEnduranceAudioBrakePB;

    Button settingsExportDatabaseButton;
    Button settingsImportDatabaseButton;

    Button settingsClearPatternsBTN;
    Button settingsClearModifiersBTN;
    Button settingsClearHistoryBTN;
    Button settingsShowIntroBTN;
    Button settingsShowNotesBTN;
    Button settingsResetSettingsBTN;
    Button settingsContactBTN;


    Button settingsSpecialThrowSequenceRemoveButton;
    Button settingsSpecialThrowSequenceAddButton;

    List<String> setSequenceForSpinner = new ArrayList<>(); //our list of Object Types

    //--------------------------END SETTINGS TAB VARIABLES----------------------------------
//--------------------------BEGIN STATS TAB VARIABLES----------------------------------
    TextView statsTotalNumberOfLabel;
    TextView statsExtenderForSeperators;
    TextView statsTotalRunsLabel;
    TextView statsTotalRunsNum;
    TextView statsEndurRunsLabel;
    TextView statsEndurRunsNum;
    TextView statsEndurPersonalBestLabel;
    TextView statsEndurPersonalBestNum;
    TextView statsEndurRunsIntentionalEndLabel;
    TextView statsEndurRunsIntentionalEndNum;
    TextView statsEndurRunsIntentionalEndPersonalBestLabel;
    TextView statsEndurRunsIntentionalEndPersonalBestNum;
    TextView statsEndurRunsUnintentionalEndLabel;
    TextView statsEndurRunsUnintentionalEndNum;
    TextView statsEndurRunsUnintentionalEndPersonalBestLabel;
    TextView statsEndurRunsUnintentionalEndPersonalBestNum;
    TextView statsInitialEndurRunsLabel;
    TextView statsInitialEndurRunsNum;
    TextView statsDrillRunsLabel;
    TextView statsDrillRunsNum;
    TextView statsDrillRunsRestartDropLabel;
    TextView statsDrillRunsRestartNum;
    TextView statsDrillRunsRestartDropDroplessLabel;
    TextView statsDrillRunsRestartDroplessNum;
    TextView statsDrillRunsDontRestartDropLabel;
    TextView statsDrillRunsDontRestartNum;
    TextView statsInitialDrillRunsLabel;
    TextView statsInitialDrillRunsNum;
    TextView statsTotalEndSpacer;

    TextView statsTotalTimeLabel;
    TextView statsTotalTimeRunsLabel;
    TextView statsTotalTimeRunsNum;
    TextView statsTotalTimeEnduranceLabel;
    TextView statsTotalTimeEnduranceNum;
    TextView statsTotalTimeEndurancePersonalBestLabel;
    TextView statsTotalTimeEndurancePersonalBestNum;
    TextView statsTotalTimeEnduranceIntentionalEndLabel;
    TextView statsTotalTimeEnduranceIntentionalEndNum;
    TextView statsTotalTimeEnduranceIntentionalEndPersonalBestLabel;
    TextView statsTotalTimeEnduranceIntentionalEndPersonalBestNum;
    TextView statsTotalTimeEnduranceUnintentionalEndLabel;
    TextView statsTotalTimeEnduranceUnintentionalEndNum;
    TextView statsTotalTimeEnduranceUnintentionalEndPersonalBestLabel;
    TextView statsTotalTimeEnduranceUnintentionalEndPersonalBestNum;
    TextView statsTotalTimeInitialEnduranceLabel;
    TextView statsTotalTimeInitialEnduranceNum;
    TextView statsTotalTimeDrillsRestartLabel;
    TextView statsTotalTimeDrillsRestartNum;
    TextView statsTotalTimeDrillsRestartDroplessLabel;
    TextView statsTotalTimeDrillsRestartDroplessNum;
    TextView statsTotalTimeDrillsDontRestartLabel;
    TextView statsTotalTimeDrillsDontRestartNum;
    TextView statsTotalTimeDrillsLabel;
    TextView statsTotalTimeDrillsNum;
    TextView statsTotalTimeInitialDrillLabel;
    TextView statsTotalTimeInitialDrillNum;
    TextView statsTotalTimeEndSpacer;

    TextView statsDaysSinceLabel;
    TextView statsDaysSinceRunLabel;
    TextView statsDaysSinceRunNum;
    TextView statsDaysSinceEnduranceLabel;
    TextView statsDaysSinceEnduranceNum;
    TextView statsDaysSinceEnduranceBrokePersonalBestLabel;
    TextView statsDaysSinceEnduranceBrokePersonalBestNum;
    TextView statsDaysSinceEnduranceIntentionalEndLabel;
    TextView statsDaysSinceEnduranceIntentionalEndNum;
    TextView statsDaysSincePersonalBestIntentionalEndLabel;
    TextView statsDaysSincePersonalBestIntentionalEndNum;
    TextView statsDaysSinceEnduranceUnintentionalEndLabel;
    TextView statsDaysSinceEnduranceUnintentionalEndNum;
    TextView statsDaysSincePersonalBestUnintentionalEndLabel;
    TextView statsDaysSincePersonalBestUnintentionalEndNum;
    TextView statsDaysSinceInitialEnduranceLabel;
    TextView statsDaysSinceInitialEnduranceNum;
    TextView statsDaysSinceDrillLabel;
    TextView statsDaysSinceDrillNum;
    TextView statsDaysSinceDrillRestartLabel;
    TextView statsDaysSinceDrillRestartNum;
    TextView statsDaysSinceDrillRestartDroplessLabel;
    TextView statsDaysSinceDrillRestartDroplessNum;
    TextView statsDaysSinceDrillDontRestartLabel;
    TextView statsDaysSinceDrillDontRestartNum;
    TextView statsDaysSinceInitialDrillLabel;
    TextView statsDaysSinceInitialDrillNum;
    TextView statsDaysSinceEndSpacer;


    TextView statsAverageNumberOfBlankPerXEditTextNumberDaysLabel;
    TextView statsAverageNumberOfBlankPerXEditTextNumberDaysDefineXLabel;
    EditText statsAverageNumberOfBlankPerXEditText;
    Button statsAverageNumberOfBlankPerXEditTextButton;
    TextView statsAverageNumberOfBlankPerXEditTextNumberDaysRunLabel;
    TextView statsAverageNumberOfBlankPerXEditTextNumberDaysRunNum;
    TextView statsAverageNumberOfBlankPerXEditTextNumberDaysEnduranceRunLabel;
    TextView statsAverageNumberOfBlankPerXEditTextNumberDaysEnduranceRunNum;
    TextView statsAverageNumberOfBlankPerXEditTextNumberDaysEnduranceRunPersonalBestLabel;
    TextView statsAverageNumberOfBlankPerXEditTextNumberDaysEnduranceRunPersonalBestNum;
    TextView statsAverageNumberOfBlankPerXEditTextNumberDaysEnduranceRunIntentionalEndLabel;
    TextView statsAverageNumberOfBlankPerXEditTextNumberDaysEnduranceRunIntentionalEndNum;
    TextView statsAverageNumberOfBlankPerXEditTextNumberDaysEnduranceRunIntentionalEndPersonalBestLabel;
    TextView statsAverageNumberOfBlankPerXEditTextNumberDaysEnduranceRunIntentionalEndPersonalBestNum;
    TextView statsAverageNumberOfBlankPerXEditTextNumberDaysEnduranceRunUnintentionalEndLabel;
    TextView statsAverageNumberOfBlankPerXEditTextNumberDaysEnduranceRunUnintentionalEndNum;
    TextView statsAverageNumberOfBlankPerXEditTextNumberDaysEnduranceRunUnintentionalEndPersonalBestLabel;
    TextView statsAverageNumberOfBlankPerXEditTextNumberDaysEnduranceRunUnintentionalEndPersonalBestNum;
    TextView statsAverageNumberOfBlankPerXEditTextNumberDaysInitialEnduranceLabel;
    TextView statsAverageNumberOfBlankPerXEditTextNumberDaysInitialEnduranceNum;
    TextView statsAverageNumberOfBlankPerXEditTextNumberDaysDrillRunLabel;
    TextView statsAverageNumberOfBlankPerXEditTextNumberDaysDrillRunNum;
    TextView statsAverageNumberOfBlankPerXEditTextNumberDaysDrillRunRestartLabel;
    TextView statsAverageNumberOfBlankPerXEditTextNumberDaysDrillRunRestartNum;
    TextView statsAverageNumberOfBlankPerXEditTextNumberDaysDrillRunRestartDroplessLabel;
    TextView statsAverageNumberOfBlankPerXEditTextNumberDaysDrillRunRestartDroplessNum;
    TextView statsAverageNumberOfBlankPerXEditTextNumberDaysDrillRunDontRestartLabel;
    TextView statsAverageNumberOfBlankPerXEditTextNumberDaysDrillRunDontRestartNum;
    TextView statsAverageNumberOfBlankPerXEditTextNumberDaysInitialDrillRunLabel;
    TextView statsAverageNumberOfBlankPerXEditTextNumberDaysInitialDrillRunNum;
    TextView statsAverageNumberOfBlankPerXEditTextNumberDaysEndSpacer;

    TextView statsNumberOfBlankInLastXEditTextNumberDaysLabel;
    TextView statsNumberOfBlankInLastXEditTextNumberDaysDefineXLabel;
    EditText statsNumberOfBlankInLastXEditText;
    Button statsNumberOfBlankInLastXEditTextButton;
    TextView statsNumberOfBlankInLastXEditTextNumberDaysRunLabel;
    TextView statsNumberOfBlankInLastXEditTextNumberDaysRunNum;
    TextView statsNumberOfBlankInLastXEditTextNumberDaysEnduranceRunLabel;
    TextView statsNumberOfBlankInLastXEditTextNumberDaysEnduranceRunNum;
    TextView statsNumberOfBlankInLastXEditTextNumberDaysEnduranceRunPersonalBestLabel;
    TextView statsNumberOfBlankInLastXEditTextNumberDaysEnduranceRunPersonalBestNum;
    TextView statsNumberOfBlankInLastXEditTextNumberDaysEnduranceRunIntentionalEndLabel;
    TextView statsNumberOfBlankInLastXEditTextNumberDaysEnduranceRunIntentionalEndNum;
    TextView statsNumberOfBlankInLastXEditTextNumberDaysEnduranceRunIntentionalEndPersonalBestLabel;
    TextView statsNumberOfBlankInLastXEditTextNumberDaysEnduranceRunIntentionalEndPersonalBestNum;
    TextView statsNumberOfBlankInLastXEditTextNumberDaysEnduranceRunUnintentionalEndLabel;
    TextView statsNumberOfBlankInLastXEditTextNumberDaysEnduranceRunUnintentionalEndNum;
    TextView statsNumberOfBlankInLastXEditTextNumberDaysEnduranceRunUnintentionalEndPersonalBestLabel;
    TextView statsNumberOfBlankInLastXEditTextNumberDaysEnduranceRunUnintentionalEndPersonalBestNum;
    TextView statsNumberOfBlankInLastXEditTextNumberDaysInitialEnduranceLabel;
    TextView statsNumberOfBlankInLastXEditTextNumberDaysInitialEnduranceNum;
    TextView statsNumberOfBlankInLastXEditTextNumberDaysDrillRunLabel;
    TextView statsNumberOfBlankInLastXEditTextNumberDaysDrillRunNum;
    TextView statsNumberOfBlankInLastXEditTextNumberDaysDrillRunRestartLabel;
    TextView statsNumberOfBlankInLastXEditTextNumberDaysDrillRunRestartNum;
    TextView statsNumberOfBlankInLastXEditTextNumberDaysDrillRunRestartDroplessLabel;
    TextView statsNumberOfBlankInLastXEditTextNumberDaysDrillRunRestartDroplessNum;
    TextView statsNumberOfBlankInLastXEditTextNumberDaysDrillRunDontRestartLabel;
    TextView statsNumberOfBlankInLastXEditTextNumberDaysDrillRunDontRestartNum;
    TextView statsNumberOfBlankInLastXEditTextNumberDaysInitialDrillRunLabel;
    TextView statsNumberOfBlankInLastXEditTextNumberDaysInitialDrillRunNum;
    TextView statsNumberOfBlankInLastXEditTextNumberDaysEndSpacer;


    TextView statsPercentEndurLabel;
    TextView statsPercentEndurEndIntentionalEndLabel;
    TextView statsPercentEndurEndIntentionalEndNum;
    TextView statsPercentEndurEndUnintentionalEndLabel;
    TextView statsPercentEndurEndUnintentionalEndNum;
    TextView statsPercentEndurAnyPersonalBestBrokeLabel;
    TextView statsPercentEndurAnyPersonalBestBrokeNum;
    TextView statsPercentEndurIntentionalEndPersonalBestBrokeLabel;
    TextView statsPercentEndurIntentionalEndPersonalBestBrokeNum;
    TextView statsPercentEndurUnintentionalEndPersonalBestBrokeLabel;
    TextView statsPercentEndurUnintentionalEndPersonalBestBrokeNum;
    TextView statsPercentEndurEndSpacer;

    TextView statsCurrentStreakLabel;
    TextView statsCurrentStreakPersonalBestLabel;
    TextView statsCurrentStreakPersonalBestNum;
    TextView statsCurrentStreakPersonalBestIntentionalEndLabel;
    TextView statsCurrentStreakPersonalBestIntentionalEndNum;
    TextView statsCurrentStreakPersonalBestUnintentionalEndLabel;
    TextView statsCurrentStreakPersonalBestUnintentionalEndNum;
    TextView statsCurrentStreakEndSpacer;

    TextView statsLongestStreakLabel;
    TextView statsLongestStreakPersonalBestLabel;
    TextView statsLongestStreakPersonalBestNum;
    TextView statsLongestStreakPersonalBestIntentionalEndLabel;
    TextView statsLongestStreakPersonalBestIntentionalEndNum;
    TextView statsLongestStreakPersonalBestUnintentionalEndLabel;
    TextView statsLongestStreakPersonalBestUnintentionalEndNum;
    TextView statsLongestStreakEndSpacer;


    TextView statsNumberOfDaysDoingAtLeastXBlankEveryYDaysLabel;
    TextView statsNumberOfDaysDoingAtLeastXBlankEveryYDaysDefineXLabel;
    EditText statsNumberOfDaysDoingAtLeastXBlankEveryYDaysXEditText;
    TextView statsNumberOfDaysDoingAtLeastXBlankEveryYDaysDefineYLabel;
    EditText statsNumberOfDaysDoingAtLeastXBlankEveryYDaysYEditText;
    Button statsNumberOfDaysDoingAtLeastXBlankEveryYDaysXEditTextButton;
    Button statsNumberOfDaysDoingAtLeastXBlankEveryYDaysYEditTextButton;
    TextView statsNumberOfDaysDoingAtLeastXBlankEveryYDaysRunLabel;
    TextView statsNumberOfDaysDoingAtLeastXBlankEveryYDaysRunNum;
    TextView statsNumberOfDaysDoingAtLeastXBlankEveryYDaysEnduranceRunLabel;
    TextView statsNumberOfDaysDoingAtLeastXBlankEveryYDaysEnduranceRunNum;
    TextView statsNumberOfDaysDoingAtLeastXBlankEveryYDaysEnduranceRunPersonalBestLabel;
    TextView statsNumberOfDaysDoingAtLeastXBlankEveryYDaysEnduranceRunPersonalBestNum;
    TextView statsNumberOfDaysDoingAtLeastXBlankEveryYDaysEnduranceRunIntentionalEndLabel;
    TextView statsNumberOfDaysDoingAtLeastXBlankEveryYDaysEnduranceRunIntentionalEndNum;
    TextView statsNumberOfDaysDoingAtLeastXBlankEveryYDaysEnduranceRunIntentionalEndPersonalBestLabel;
    TextView statsNumberOfDaysDoingAtLeastXBlankEveryYDaysEnduranceRunIntentionalEndPersonalBestNum;
    TextView statsNumberOfDaysDoingAtLeastXBlankEveryYDaysEnduranceRunUnintentionalEndLabel;
    TextView statsNumberOfDaysDoingAtLeastXBlankEveryYDaysEnduranceRunUnintentionalEndNum;
    TextView statsNumberOfDaysDoingAtLeastXBlankEveryYDaysEnduranceRunUnintentionalEndPersonalBestLabel;
    TextView statsNumberOfDaysDoingAtLeastXBlankEveryYDaysEnduranceRunUnintentionalEndPersonalBestNum;
    TextView statsNumberOfDaysDoingAtLeastXBlankEveryYDaysInitialEnduranceLabel;
    TextView statsNumberOfDaysDoingAtLeastXBlankEveryYDaysInitialEnduranceNum;
    TextView statsNumberOfDaysDoingAtLeastXBlankEveryYDaysDrillRunLabel;
    TextView statsNumberOfDaysDoingAtLeastXBlankEveryYDaysDrillRunNum;
    TextView statsNumberOfDaysDoingAtLeastXBlankEveryYDaysDrillRunRestartLabel;
    TextView statsNumberOfDaysDoingAtLeastXBlankEveryYDaysDrillRunRestartNum;
    TextView statsNumberOfDaysDoingAtLeastXBlankEveryYDaysDrillRunRestartDroplessLabel;
    TextView statsNumberOfDaysDoingAtLeastXBlankEveryYDaysDrillRunRestartDroplessNum;
    TextView statsNumberOfDaysDoingAtLeastXBlankEveryYDaysDrillRunDontRestartLabel;
    TextView statsNumberOfDaysDoingAtLeastXBlankEveryYDaysDrillRunDontRestartNum;
    TextView statsNumberOfDaysDoingAtLeastXBlankEveryYDaysInitialDrillRunLabel;
    TextView statsNumberOfDaysDoingAtLeastXBlankEveryYDaysInitialDrillRunNum;
    TextView statsNumberOfDaysDoingAtLeastXBlankEveryYDaysEndSpacer;

    TextView statsMostDaysDoingAtLeastXBlankEveryYDaysLabel;
    TextView statsMostDaysDoingAtLeastXBlankEveryYDaysDefineXLabel;
    EditText statsMostDaysDoingAtLeastXBlankEveryYDaysXEditText;
    TextView statsMostDaysDoingAtLeastXBlankEveryYDaysDefineYLabel;
    EditText statsMostDaysDoingAtLeastXBlankEveryYDaysYEditText;
    Button statsMostDaysDoingAtLeastXBlankEveryYDaysXEditTextButton;
    Button statsMostDaysDoingAtLeastXBlankEveryYDaysYEditTextButton;
    TextView statsMostDaysDoingAtLeastXBlankEveryYDaysRunLabel;
    TextView statsMostDaysDoingAtLeastXBlankEveryYDaysRunNum;
    TextView statsMostDaysDoingAtLeastXBlankEveryYDaysEnduranceRunLabel;
    TextView statsMostDaysDoingAtLeastXBlankEveryYDaysEnduranceRunNum;
    TextView statsMostDaysDoingAtLeastXBlankEveryYDaysEnduranceRunPersonalBestLabel;
    TextView statsMostDaysDoingAtLeastXBlankEveryYDaysEnduranceRunPersonalBestNum;
    TextView statsMostDaysDoingAtLeastXBlankEveryYDaysEnduranceRunIntentionalEndLabel;
    TextView statsMostDaysDoingAtLeastXBlankEveryYDaysEnduranceRunIntentionalEndNum;
    TextView statsMostDaysDoingAtLeastXBlankEveryYDaysEnduranceRunIntentionalEndPersonalBestLabel;
    TextView statsMostDaysDoingAtLeastXBlankEveryYDaysEnduranceRunIntentionalEndPersonalBestNum;
    TextView statsMostDaysDoingAtLeastXBlankEveryYDaysEnduranceRunUnintentionalEndLabel;
    TextView statsMostDaysDoingAtLeastXBlankEveryYDaysEnduranceRunUnintentionalEndNum;
    TextView statsMostDaysDoingAtLeastXBlankEveryYDaysEnduranceRunUnintentionalEndPersonalBestLabel;
    TextView statsMostDaysDoingAtLeastXBlankEveryYDaysEnduranceRunUnintentionalEndPersonalBestNum;
    TextView statsMostDaysDoingAtLeastXBlankEveryYDaysInitialEnduranceLabel;
    TextView statsMostDaysDoingAtLeastXBlankEveryYDaysInitialEnduranceNum;
    TextView statsMostDaysDoingAtLeastXBlankEveryYDaysDrillRunLabel;
    TextView statsMostDaysDoingAtLeastXBlankEveryYDaysDrillRunNum;
    TextView statsMostDaysDoingAtLeastXBlankEveryYDaysDrillRunRestartLabel;
    TextView statsMostDaysDoingAtLeastXBlankEveryYDaysDrillRunRestartNum;
    TextView statsMostDaysDoingAtLeastXBlankEveryYDaysDrillRunRestartDroplessLabel;
    TextView statsMostDaysDoingAtLeastXBlankEveryYDaysDrillRunRestartDroplessNum;
    TextView statsMostDaysDoingAtLeastXBlankEveryYDaysDrillRunDontRestartLabel;
    TextView statsMostDaysDoingAtLeastXBlankEveryYDaysDrillRunDontRestartNum;
    TextView statsMostDaysDoingAtLeastXBlankEveryYDaysInitialDrillRunLabel;
    TextView statsMostDaysDoingAtLeastXBlankEveryYDaysInitialDrillRunNum;
    TextView statsMostDaysDoingAtLeastXBlankEveryYDaysEndSpacer;


    TextView statsUniqueComboModifiersAndEntriesLabel;
    TextView statsUniqueComboAnyLabel;
    TextView statsUniqueComboAnyNum;
    TextView statsUniqueComboEndurLabel;
    TextView statsUniqueComboEndurNum;
    TextView statsUniqueComboEndurIntentionalEndLabel;
    TextView statsUniqueComboEndurIntentionalEndNum;
    TextView statsUniqueComboEndurUnintentionalEndLabel;
    TextView statsUniqueComboEndurUnintentionalEndNum;
    TextView statsUniqueComboDrillLabel;
    TextView statsUniqueComboDrillNum;
    TextView statsUniqueComboDrillRestartLabel;
    TextView statsUniqueComboDrillRestartNum;
    TextView statsUniqueComboDrillDontRestartLabel;
    TextView statsUniqueComboDrillDontRestartNum;
    TextView statsUniqueComboEndurAndDrillLabel;
    TextView statsUniqueComboEndurAndDrillNum;
    TextView statsUniqueComboEndSpacer;


    //--------------------------END STATS TAB VARIABLES----------------------------------


    private PowerManager.WakeLock wakeLock;


    @Override
    public void onBackPressed() {
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
//                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        //the back button shows a dialog that asks the user if they want to minimize the app or close it and
        //          end their session(if they are in one only), if they choose to close it, then set the session end time
        //          based on the time+length of the most recent run
        showMinimizeOrCloseDialog();


    }

    public void showMinimizeOrCloseDialog() {
        new AlertDialog.Builder(MainActivity.this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Leave?")
                .setMessage("Would you like to minimize or close? If you close and you are in a session, the session will end.")
                .setPositiveButton("Close", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        killApp();

                    }
                })

                .setNegativeButton("Minimize", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        minimizeApp();
                    }
                })

                .setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .show();
    }

    public void killApp() {
        //if we are currently in a session, then we want to use the current date/time to end it
        if (runCurrentlyInSession()) {
            String mostRecentID = getColumnFromDB("SESSIONS", "ID").get(getColumnFromDB("SESSIONS", "ID").size() - 1);
            ////Log.d("mostRecentID", mostRecentID);
            myDb.updateData("SESSIONS", "ENDTIME", "ID", mostRecentID, dateAndTimeOfMostRecentRun());
        }

        int pid = android.os.Process.myPid();
        android.os.Process.killProcess(pid);

        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        startActivity(intent);

    }

    public void minimizeApp() {
        Intent startMain = new Intent(Intent.ACTION_MAIN);
        startMain.addCategory(Intent.CATEGORY_HOME);
        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(startMain);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("onCreate", "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); //attaches us to our activity_main layout

        //the wakeLock is used when we go into a timer, it makes sure that even if we turn the screen off, the timer will continue
        //      to go.
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "DoNotDimScreen");

        //mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);



        drillCompleteSound = MediaPlayer.create(this, R.raw.good);
        enduranceIntentionalEndPersonalBestBrokenSound = MediaPlayer.create(this, R.raw.guitarnewrecord);
        enduranceUnintentionalEndPersonalBestBrokenSound = MediaPlayer.create(this, R.raw.newrecord);

        File file = this.getDatabasePath(dbName);
        if (!file.exists()) {
            Log.d("didntexist", "5");
            //if the database doesn't currently exist, then this is the first time the app has been run and we need to add
            //      the stuff to the database to make the default settings
            myDb = new DatabaseHelper(this);//creates an object from our database class over in DatabaseHelper
            addFirstTimeRunDatabaseData();

        } else {
            myDb = new DatabaseHelper(this);//creates an object from our database class over in DatabaseHelper
        }


        setupMainInputs(); //sets up the autocompleteTextViews for patterns/modifiers


        prepareHistoryTabActivity();

        prepareRunTabActivity();

        setVisiblityDrillSettings(View.GONE);

        prepareAddTabActivity(); //this gets everything ready that will be used in the Add Tabs Activity
        //Log.d("z", "1");

        updateAddPatternNumberOfObjectsFromDB();

        fillObjectTypeATVsfromDB();

        fillPatternMainInputsFromDB();

        setDefaultsToSelectionsFromPreviousRun();

        prepareSettingsTabActivity();

        setupTabHost(); //sets up the 5 tabs (juggle, history, add, settings, and stats)

        setupStats();

        fillModifierMultiAutocompleteTextViewsFromDB();


        if ((getCellFromDB("SETTINGS", "MISC", "ID", "9").equals("on"))) {
            showIntroDialog();
        }

        refreshStats();

    }

    public void showIntroDialog() {
        View view = (LayoutInflater.from(MainActivity.this)).inflate(R.layout.introdialog, null);

        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(MainActivity.this);
        alertBuilder.setView(view)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Welcome!\n");

        TextView introDialogTV = (TextView) view.findViewById(R.id.introDialogTV);
        introDialogTV.setText("Formic is a timer with an integrated database that is designed for keeping track of juggling progress.\n\n" +
                "To get started, use the 'Main Input' section at the top of the screen to choose a pattern, number of objects, prop" +
                " type, and any number of modifiers. Once the 'Main Input' is filled in, click the 'JUG' tab to begin a run.\n\nThe" +
                " history of the currently selected stuff in the 'Main Input' can be viewed inn the 'HIST' tab.  The 'ADD' tab is used to add" +
                " new patterns and modifiers, as well as view their details. At any point, help is available by holding your finger for 2" +
                " seconds on buttons, texts, and inputs.");
        final CheckBox introDialogShowAutomaticallyCheckBox = (CheckBox) view.findViewById(R.id.introDialogShowAutomaticallyCheckBox);
        if ((getCellFromDB("SETTINGS", "MISC", "ID", "9").equals("on"))) {
            introDialogShowAutomaticallyCheckBox.setChecked(true);
        }
        introDialogShowAutomaticallyCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!introDialogShowAutomaticallyCheckBox.isChecked()) {
                    updateDataInDB("SETTINGS", "MISC", "ID", "9", "off"); //keeps track of whether or not the intro screen should be shown (id = 9)

                }
            }
        });

        Button introDialogAddSiteswaps = (Button) view.findViewById(R.id.introDialogAddSiteswaps);
        introDialogAddSiteswaps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {






                String bigListOfSiteswaps = "(2T,2T)(2x,2x)\n" +
                        "(2T,2T)\n" +
                        "(4,2x)(2x,0)*\n" +
                        "(4,2x)(2x,0)\n" +
                        "(4,2x)(2x,4)\n" +
                        "(4,4)(0,0)(4x,4x)(0,0)\n" +
                        "(4,4)(0,0)\n" +
                        "(4x,4x)(0,0)\n" +
                        "1\n" +
                        "2\n" +
                        "2T0\n" +
                        "2T\n" +
                        "3001\n" +
                        "300\n" +
                        "30313\n" +
                        "3\n" +
                        "40001\n" +
                        "400020\n" +
                        "4000\n" +
                        "42\n" +
                        "441\n" +
                        "4\n" +
                        "500001\n" +
                        "515001\n" +
                        "51\n" +
                        "5\n" +
                        "602040\n" +
                        "60\n" +
                        "6\n" +
                        "8000\n" +
                        "9\n" +
                        "[11]\n" +
                        "[2T2T]0\n" +
                        "[2T2T]\n" +
                        "[33]00\n" +
                        "[33]00[11]\n" +
                        "[33]\n" +
                        "[33][11]\n" +
                        "[42]04020\n" +
                        "[44]000\n" +
                        "[44]000[11]\n" +
                        "[44]\n" +
                        "[44][44][11]" +
                        "34030\n" +
                        "35011\n" +
                        "35020\n" +
                        "41131\n" +
                        "41401\n" +
                        "45001\n" +
                        "51112\n" +
                        "31\n" +
                        "40\n" +
                        "51130\n" +
                        "51400\n" +
                        "52030\n" +
                        "55000\n" +
                        "61111\n" +
                        "61120\n" +
                        "61201\n" +
                        "312\n" +
                        "330\n" +
                        "411\n" +
                        "420\n" +
                        "501\n" +
                        "600\n" +
                        "61300\n" +
                        "62011\n" +
                        "62020\n" +
                        "63001\n" +
                        "64000\n" +
                        "50140\n" +
                        "50500\n" +
                        "70111\n" +
                        "70120\n" +
                        "70201\n" +
                        "70300\n" +
                        "80011\n" +
                        "80020\n" +
                        "90001\n" +
                        "3401\n" +
                        "4112\n" +
                        "4130\n" +
                        "4400\n" +
                        "5111\n" +
                        "5120\n" +
                        "5201\n" +
                        "5300\n" +
                        "6011\n" +
                        "6020\n" +
                        "7001\n" +
                        "340401\n" +
                        "350130\n" +
                        "350400\n" +
                        "360111\n" +
                        "360120\n" +
                        "360201\n" +
                        "360300\n" +
                        "414030\n" +
                        "415011\n" +
                        "415020\n" +
                        "450030\n" +
                        "460011\n" +
                        "460020\n" +
                        "511401\n" +
                        "515001\n" +
                        "520401\n" +
                        "560001\n" +
                        "611130\n" +
                        "611400\n" +
                        "612030\n" +
                        "615000\n" +
                        "620130\n" +
                        "620400\n" +
                        "630030\n" +
                        "660000\n" +
                        "711111\n" +
                        "711120\n" +
                        "711201\n" +
                        "711300\n" +
                        "712011\n" +
                        "712020\n" +
                        "713001\n" +
                        "714000\n" +
                        "720111\n" +
                        "720120\n" +
                        "720201\n" +
                        "720300\n" +
                        "730011\n" +
                        "730020\n" +
                        "740001\n" +
                        "750000\n" +
                        "506001\n" +
                        "601500\n" +
                        "606000\n" +
                        "801111\n" +
                        "801120\n" +
                        "801201\n" +
                        "801300\n" +
                        "802011\n" +
                        "802020\n" +
                        "803001\n" +
                        "804000\n" +
                        "900111\n" +
                        "900120\n" +
                        "900201\n" +
                        "900300\n" +
                        "3401411\n" +
                        "3405011\n" +
                        "3601400\n" +
                        "3701111\n" +
                        "3703001\n" +
                        "4160111\n" +
                        "5111411\n" +
                        "7111130\n" +
                        "7700000\n" +
                        "8111111\n" +
                        "8140001\n" +
                        "8500001\n" +
                        "91111111\n" +
                        "91113001\n" +
                        "91140001\n" +
                        "414050130\n" +
                        "5070011\n" +
                        "6011501\n" +
                        "6016001\n" +
                        "7011140\n" +
                        "7030040\n" +
                        "7070000\n" +
                        "9030011\n" +
                        "7007000\n" +
                        "70111501\n" +
                        "507006000\n" +
                        "701170011\n" +
                        "423\n" +
                        "522\n" +
                        "531\n" +
                        "450\n" +
                        "4440\n" +
                        "4512\n" +
                        "4530\n" +
                        "5241\n" +
                        "5340\n" +
                        "5511\n" +
                        "5520\n" +
                        "6222\n" +
                        "6231\n" +
                        "6312\n" +
                        "6330\n" +
                        "6411\n" +
                        "7131\n" +
                        "7401\n" +
                        "612\n" +
                        "630\n" +
                        "711\n" +
                        "603\n" +
                        "801\n" +
                        "5700\n" +
                        "8130\n" +
                        "9111\n" +
                        "9300\n" +
                        "6051\n" +
                        "7041\n" +
                        "8013\n" +
                        "8040\n" +
                        "34512\n" +
                        "45141\n" +
                        "45501\n" +
                        "46131\n" +
                        "46302\n" +
                        "46401\n" +
                        "52440\n" +
                        "52512\n" +
                        "52530\n" +
                        "55140\n" +
                        "55203\n" +
                        "56130\n" +
                        "63141\n" +
                        "63501\n" +
                        "64203\n" +
                        "66111\n" +
                        "72330\n" +
                        "73131\n" +
                        "74130\n" +
                        "51630\n" +
                        "55050\n" +
                        "57030\n" +
                        "61251\n" +
                        "61350\n" +
                        "61611\n" +
                        "81312\n" +
                        "81330\n" +
                        "83031\n" +
                        "84012\n" +
                        "84030\n" +
                        "61701\n" +
                        "91401\n" +
                        "470251\n" +
                        "470350\n" +
                        "470611\n" +
                        "70161\n" +
                        "70701\n" +
                        "90141\n" +
                        "90303\n" +
                        "90501\n" +
                        "451512\n" +
                        "461241\n" +
                        "461511\n" +
                        "471231\n" +
                        "471312\n" +
                        "525141\n" +
                        "526122\n" +
                        "526131\n" +
                        "622512\n" +
                        "631512\n" +
                        "722241\n" +
                        "723141\n" +
                        "731241\n" +
                        "615600\n" +
                        "713151\n" +
                        "913131\n" +
                        "4516131\n" +
                        "6316131\n" +
                        "453053403\n" +
                        "6316166001\n" +
                        "451640164500\n" +
                        "564006415041\n" +
                        "714014714700\n" +
                        "741701740041\n" +
                        "7330730370330\n" +
                        "5505051\n" +
                        "6161601\n" +
                        "7141404\n" +
                        "71701701\n" +
                        "6050505\n" +
                        "61701\n" +
                        "91401\n" +
                        "70251\n" +
                        "70350\n" +
                        "70611\n" +
                        "70161\n" +
                        "53\n" +
                        "62\n" +
                        "71\n" +
                        "534\n" +
                        "552\n" +
                        "633\n" +
                        "642\n" +
                        "5551\n" +
                        "5623\n" +
                        "5641\n" +
                        "6352\n" +
                        "6451\n" +
                        "6622\n" +
                        "6631\n" +
                        "7333\n" +
                        "7342\n" +
                        "7423\n" +
                        "7441\n" +
                        "7522\n" +
                        "7531\n" +
                        "5560\n" +
                        "561\n" +
                        "723\n" +
                        "741\n" +
                        "8223\n" +
                        "8313\n" +
                        "5660\n" +
                        "5714\n" +
                        "9123\n" +
                        "5713\n" +
                        "6712\n" +
                        "8233\n" +
                        "8413\n" +
                        "8512\n" +
                        "92233\n" +
                        "92413\n" +
                        "93133\n" +
                        "57162\n" +
                        "58134\n" +
                        "58152\n" +
                        "9124\n" +
                        "9151\n" +
                        "67223\n" +
                        "67241\n" +
                        "67313\n" +
                        "73352\n" +
                        "73451\n" +
                        "73622\n" +
                        "73631\n" +
                        "74252\n" +
                        "74612\n" +
                        "75251\n" +
                        "77222\n" +
                        "77231\n" +
                        "77312\n" +
                        "55550\n" +
                        "55613\n" +
                        "56252\n" +
                        "56612\n" +
                        "57233\n" +
                        "57242\n" +
                        "57413\n" +
                        "57512\n" +
                        "63551\n" +
                        "63623\n" +
                        "63641\n" +
                        "64613\n" +
                        "66251\n" +
                        "83333\n" +
                        "83342\n" +
                        "83423\n" +
                        "83441\n" +
                        "83522\n" +
                        "83531\n" +
                        "84233\n" +
                        "84512\n" +
                        "85223\n" +
                        "85241\n" +
                        "85313\n" +
                        "86222\n" +
                        "86231\n" +
                        "86312\n" +
                        "58133\n" +
                        "58142\n" +
                        "66161\n" +
                        "68123\n" +
                        "68141\n" +
                        "72461\n" +
                        "72722\n" +
                        "72731\n" +
                        "75161\n" +
                        "78122\n" +
                        "78131\n" +
                        "92333\n" +
                        "92342\n" +
                        "92423\n" +
                        "92441\n" +
                        "92522\n" +
                        "92531\n" +
                        "94133\n" +
                        "94142\n" +
                        "95123\n" +
                        "95141\n" +
                        "96122\n" +
                        "96131\n" +
                        "728123\n" +
                        "81362\n" +
                        "81461\n" +
                        "81722\n" +
                        "81731\n" +
                        "91334\n" +
                        "91352\n" +
                        "91424\n" +
                        "91451\n" +
                        "91622\n" +
                        "91631\n" +
                        "818123\n" +
                        "571731\n" +
                        "6716161\n" +
                        "566151\n" +
                        "575151\n" +
                        "577131\n" +
                        "733551\n" +
                        "747141\n" +
                        "736251\n" +
                        "4567123\n" +
                        "5561551\n" +
                        "6357142\n" +
                        "6366151\n" +
                        "6374152\n" +
                        "6461641\n" +
                        "44557133\n" +
                        "56262525\n" +
                        "56716151\n" +
                        "8441841481441\n" +
                        "64\n" +
                        "73\n" +
                        "82\n" +
                        "91\n" +
                        "a0\n" +
                        "645\n" +
                        "663\n" +
                        "744\n" +
                        "753\n" +
                        "672\n" +
                        "834\n" +
                        "852\n" +
                        "933\n" +
                        "942\n" +
                        "6771\n" +
                        "6825\n" +
                        "6861\n" +
                        "6915\n" +
                        "6662\n" +
                        "6734\n" +
                        "6752\n" +
                        "7463\n" +
                        "7562\n" +
                        "7733\n" +
                        "7742\n" +
                        "8444\n" +
                        "8453\n" +
                        "8534\n" +
                        "8552\n" +
                        "8633\n" +
                        "8642\n" +
                        "66661\n" +
                        "66724\n" +
                        "66751\n" +
                        "67363\n" +
                        "67561\n" +
                        "67723\n" +
                        "67741\n" +
                        "68344\n" +
                        "68353\n" +
                        "68524\n" +
                        "68551\n" +
                        "68623\n" +
                        "68641\n" +
                        "6671\n" +
                        "6824\n" +
                        "6851\n" +
                        "7571\n" +
                        "7823\n" +
                        "7841\n" +
                        "9344\n" +
                        "9353\n" +
                        "9524\n" +
                        "9551\n" +
                        "9623\n" +
                        "9641\n" +
                        "7922\n" +
                        "7931\n" +
                        "66815\n" +
                        "68273\n" +
                        "68813\n" +
                        "69245\n" +
                        "69263\n" +
                        "69515\n" +
                        "69713\n" +
                        "8912\n" +
                        "9281\n" +
                        "668174\n" +
                        "669155\n" +
                        "669164\n" +
                        "74662\n" +
                        "74734\n" +
                        "74752\n" +
                        "75661\n" +
                        "75724\n" +
                        "75751\n" +
                        "77362\n" +
                        "77461\n" +
                        "77731\n" +
                        "78334\n" +
                        "78352\n" +
                        "78424\n" +
                        "78451\n" +
                        "78631\n" +
                        "84463\n" +
                        "84562\n" +
                        "84733\n" +
                        "85363\n" +
                        "85561\n" +
                        "85723\n" +
                        "85741\n" +
                        "86362\n" +
                        "86461\n" +
                        "86731\n" +
                        "88333\n" +
                        "94444\n" +
                        "66670\n" +
                        "66814\n" +
                        "66850\n" +
                        "67570\n" +
                        "67813\n" +
                        "67840\n" +
                        "66814\n" +
                        "69244\n" +
                        "69253\n" +
                        "69514\n" +
                        "69550\n" +
                        "69613\n" +
                        "75670\n" +
                        "75814\n" +
                        "75850\n" +
                        "77272\n" +
                        "77470\n" +
                        "77812\n" +
                        "77830\n" +
                        "79234\n" +
                        "79252\n" +
                        "79414\n" +
                        "79450\n" +
                        "79612\n" +
                        "79630\n" +
                        "83572\n" +
                        "83833\n" +
                        "85570\n" +
                        "85813\n" +
                        "85840\n" +
                        "86272\n" +
                        "86470\n" +
                        "86812\n" +
                        "86830\n" +
                        "89233\n" +
                        "89413\n" +
                        "89440\n" +
                        "89512\n" +
                        "89530\n" +
                        "77911\n" +
                        "83923\n" +
                        "83941\n" +
                        "84913\n" +
                        "84940\n" +
                        "86911\n" +
                        "66805\n" +
                        "68173\n" +
                        "69145\n" +
                        "69163\n" +
                        "69505\n" +
                        "69703\n" +
                        "75805\n" +
                        "78172\n" +
                        "78802\n" +
                        "79135\n" +
                        "79162\n" +
                        "79405\n" +
                        "79702\n" +
                        "92473\n" +
                        "92572\n" +
                        "92833\n" +
                        "95173\n" +
                        "95803\n" +
                        "96172\n" +
                        "96802\n" +
                        "99133\n" +
                        "99403\n" +
                        "99502\n" +
                        "78181\n" +
                        "78901\n" +
                        "92923\n" +
                        "92941\n" +
                        "94903\n" +
                        "96181\n" +
                        "96901\n" +
                        "79180\n" +
                        "68074\n" +
                        "69055\n" +
                        "81772\n" +
                        "81835\n" +
                        "81862\n" +
                        "88072\n" +
                        "89035\n" +
                        "89062\n" +
                        "91474\n" +
                        "91672\n" +
                        "91834\n" +
                        "91852\n" +
                        "91681\n" +
                        "91924\n" +
                        "746625\n" +
                        "747741\n" +
                        "773355\n" +
                        "774714\n" +
                        "777711\n" +
                        "824466\n" +
                        "6777161\n" +
                        "7572752\n" +
                        "7748144\n" +
                        "8448641\n" +
                        "8577161\n" +
                        "8648144\n" +
                        "9494414\n" +
                        "9595151\n" +
                        "9552752\n" +
                        "9558152\n" +
                        "9597122\n" +
                        "9952712\n" +
                        "a572722\n" +
                        "a772711\n" +
                        "55668244\n" +
                        "55668514\n" +
                        "67891234\n" +
                        "95851714\n" +
                        "b4a33333\n" +
                        "567891234\n" +
                        "685716714\n" +
                        "747771714\n" +
                        "758517741\n" +
                        "774717741\n" +
                        "855815661\n" +
                        "b5b515151\n" +
                        "b444b333444\n" +
                        "66671668144\n" +
                        "d1517191b1\n" +
                        "757173\n" +
                        "777171\n" +
                        "935373\n" +
                        "847182\n" +
                        "7266716\n" +
                        "83571637\n" +
                        "85716814\n" +
                        "75\n" +
                        "84\n" +
                        "93\n" +
                        "a2\n" +
                        "b1\n" +
                        "c0\n" +
                        "756\n" +
                        "774\n" +
                        "855\n" +
                        "864\n" +
                        "945\n" +
                        "783\n" +
                        "963\n" +
                        "882\n" +
                        "936\n" +
                        "891\n" +
                        "7746\n" +
                        "7773\n" +
                        "8646\n" +
                        "8673\n" +
                        "8844\n" +
                        "8853\n" +
                        "9555\n" +
                        "9645\n" +
                        "9663\n" +
                        "9744\n" +
                        "9753\n" +
                        "9915\n" +
                        "75756\n" +
                        "77466\n" +
                        "77475\n" +
                        "77736\n" +
                        "85566\n" +
                        "88536\n" +
                        "88833\n" +
                        "96636\n" +
                        "99444\n" +
                        "a5555\n" +
                        "b6661\n" +
                        "777366\n" +
                        "777771\n" +
                        "778851\n" +
                        "868671\n" +
                        "869661\n" +
                        "884466\n" +
                        "888822\n" +
                        "888831\n" +
                        "979731\n" +
                        "999333\n" +
                        "999441\n" +
                        "b66661\n" +
                        "b97531\n" +
                        "979191\n" +
                        "8686716\n" +
                        "8696616\n" +
                        "86\n" +
                        "95\n" +
                        "a4\n" +
                        "b3\n" +
                        "c2\n" +
                        "d1\n" +
                        "e0\n" +
                        "867\n" +
                        "885\n" +
                        "966\n" +
                        "975\n" +
                        "8884\n" +
                        "9667\n" +
                        "9955\n" +
                        "9964\n" +
                        "a666\n" +
                        "a864\n" +
                        "88883\n" +
                        "96677\n" +
                        "97577\n" +
                        "97973\n" +
                        "99944\n" +
                        "aa555\n" +
                        "b6666\n" +
                        "b9555\n" +
                        "995577\n" +
                        "996477\n" +
                        "888891\n" +
                        "979395\n" +
                        "979791\n" +
                        "8888881\n" +
                        "8889961\n" +
                        "8897971\n" +
                        "9955966\n" +
                        "9999292\n" +
                        "9992992\n" +
                        "97979717\n" +
                        "99999191\n" +
                        "99991991\n" +
                        "99919991\n" +
                        "(2x,2x)\n" +
                        "(4,2x)(2x,0)(2x,4)(0,2x)\n" +

                        "(4,2)\n" +
                        "(4x,2x)\n" +
                        "(2x,4)*\n" +
                        "(4,4)(4,0)\n" +
                        "(4,6x)(2x,0)\n" +
                        "(4x,4x)(4,0)\n" +
                        "(4x,6x)(2,0)\n" +
                        "(6,4x)(2x,0)\n" +
                        "(6,4)(2,0)\n" +
                        "(6,2)(2,2)\n" +
                        "(6,2)(2x,2x)\n" +
                        "(8,2x)(2x,0)\n" +
                        "(6,0)(2,4)\n" +
                        "(6,0)(4x,2x)\n" +
                        "(4,4)\n" +
                        "(4x,4x)\n" +
                        "(4,6x)(2x,4)\n" +
                        "(4,6x)(4x,2)\n" +
                        "(4,6)(4,2)\n" +
                        "(4,6)(2x,4x)\n" +
                        "(4x,6x)(2,4)\n" +
                        "(4x,6x)(4x,2x)\n" +
                        "(4x,6)(4,2x)\n" +
                        "(4x,6)(2,4x)\n" +
                        "(2x,6x)\n" +
                        "(2,6)(2x,6x)\n" +
                        "(2x,8x)(2x,4x)\n" +
                        "(2x,8)(4,2x)\n" +
                        "(4x,6)(0,6x)\n" +
                        "(4x,6x)(6,0)\n" +
                        "(6x,2x)*(2,4)\n" +
                        "(2x,8)(2x,4)*\n" +
                        "(6x,2)(2x,6)*\n" +
                        "(8x,2x)(2x,4x)(2x,6x)\n" +
                        "(2x,8)(4,2)(8,2)(4,2x)\n" +
                        "(6x,4)(0,6x)\n" +
                        "(6,4)(6,0)\n" +
                        "(8,2)(2x,4x)\n" +
                        "(8x,2x)(4x,2x)\n" +
                        "(4x,6x)\n" +
                        "(6,4)\n" +
                        "(8x,2x)\n" +
                        "(4,ax)(4x,2x)\n" +
                        "(8x,2x)(2,8)\n" +
                        "(8,2x)(4x,6)\n" +
                        "(8,2x)(6x,4)\n" +
                        "(ax,2x)(6x,2x)\n" +
                        "(ax,2x)(6x,2x)(8x,2x)\n" +
                        "(ax,2x)(ax,2x)(4x,2x)\n" +
                        "(cx,2x)(8x,2x)(4x,2x)\n" +
                        "(8x,2x)(4x,6x)\n" +
                        "(8x,2x)(6,4)\n" +
                        "(2x,8)(8,2x)\n" +
                        "(8x,2)(8,2x)\n" +
                        "(6,6)(2x,6)(6,4x)\n" +
                        "(6x,6x)(2,6x)(6x,4)\n" +
                        "(6,8x)(6x,2)(2x,6x)\n" +
                        "(6x,8x)(2x,8x(4x,2x)\n" +
                        "(6,6)(6,2x)*\n" +
                        "(6,4x)(6,4)*\n" +
                        "(6x,4)(4,6)*\n" +
                        "(6x,4)(6x,4x)*\n" +
                        "(8x,4)(4,4)*\n" +
                        "(8x,6)(2x,4x)*\n" +
                        "(8x,6x)(2x,4)*\n" +
                        "(4x,6)(8x,4x)(4x,6x)(2,6x)\n" +
                        "(2x,8x)(4x,6)*\n" +
                        "(8,2x)(4,6)*\n" +
                        "(8,2x)(6x,4x)*\n" +
                        "(2,8)(2x,8)*\n" +
                        "(4,8)\n" +
                        "(6,6)\n" +
                        "(6x,6x)\n" +
                        "(4x,8x)\n" +
                        "(6x,8)(4,6x)\n" +
                        "(6x,8)(6,4x)\n" +
                        "(8x,4x)(4,8)\n" +
                        "(8,6x)(4x,6)\n" +
                        "(8x,6x)(4x,6x)\n" +
                        "(8,6x)(6x,4)\n" +
                        "(8x,6)(6,4x)\n" +
                        "(8x,6x)(6,4)\n" +
                        "(8x,6)(4,6x)\n" +
                        "(8,6)(6x,4x)\n" +
                        "(8x,6)(8,2x)\n" +
                        "(8,8)(4,4)\n" +
                        "(6x,8x)\n" +
                        "(8,6)\n" +
                        "(6x,8)(8,6x)\n" +
                        "(8,6x)(6x,8)\n" +
                        "(8x,6)(6,8x)\n" +
                        "[32]\n" +
                        "1[11][42]\n" +
                        "1[51]2\n" +
                        "20[43]\n" +
                        "20[61]\n" +
                        "1[42]2\n" +
                        "22[32]\n" +
                        "23[31]\n" +
                        "24[21]\n" +
                        "521[43]0\n" +
                        "[31][21]2\n" +
                        "1[53]52\n" +
                        "1[63]42\n" +
                        "1[65]22\n" +
                        "23[53]3\n" +
                        "23[54]2\n" +
                        "1[54]2\n" +
                        "24[54]1\n" +
                        "23[43]\n" +
                        "22[53]\n" +
                        "20[64]\n" +
                        "20[73]\n" +
                        "25[53]1\n" +
                        "26[43]1\n" +
                        "1[75]12\n" +
                        "22[63]3\n" +
                        "22[64]2\n" +
                        "24[64]0\n" +
                        "25[63]0\n" +
                        "27[43]0\n" +
                        "22[74]1\n" +
                        "23[74]0\n" +
                        "1[93]12\n" +
                        "22[83]1\n" +
                        "23[83]0\n" +
                        "1[76]02\n" +
                        "20[94]1\n" +
                        "1[74]242\n" +
                        "1[76]312\n" +
                        "1[65]62\n" +
                        "1[75]52\n" +
                        "[65]22\n" +
                        "24[64]4\n" +
                        "24[65]3\n" +
                        "25[63]4\n" +
                        "25[65]2\n" +
                        "26[64]2\n" +
                        "623[54]\n" +
                        "24[75]34\n" +
                        "24[76]33\n" +
                        "24[85]344\n" +
                        "24[95]3444\n" +
                        "24[54]\n" +
                        "25[53]\n" +
                        "26[43]\n" +
                        "1[75]2\n" +
                        "23[64]\n" +
                        "1[84]2\n" +
                        "23[73]\n" +
                        "22[65]\n" +
                        "22[83]\n" +
                        "1[76]42\n" +
                        "22[43][54]\n" +
                        "22[53][53]\n" +
                        "24[76]731\n" +
                        "24[87]333\n" +
                        "24[98]3333\n" +
                        "24[22]3[543]\n" +
                        "24[22]3[764]41\n" +
                        "24[22]3[764]81716714\n" +
                        "7323[22]3[753]3\n" +
                        "24[22]3[765]31\n" +
                        "24[22]3[222]3[7654]01\n" +
                        "25[56]\n" +
                        "26[64]\n" +
                        "27[54]\n" +
                        "25[75]5\n" +
                        "25[76]4\n" +
                        "26[74]5\n" +
                        "27[74]4\n" +
                        "827[43]\n" +
                        "25[22]4[654]\n" +
                        "25[22]7[543]\n" +
                        "25[87]44\n" +
                        "25[65]\n" +
                        "24[654]\n" +
                        "25[22]5[756]2\n" +
                        "23[53]52\n" +
                        "23[54]51\n" +
                        "23[64]23\n" +
                        "23[64]41\n" +
                        "23[65]31\n" +
                        "24[65]12\n" +
                        "22[54]61\n" +
                        "(4x,2)(2,[64x])(2x,4x)\n" +
                        "(4,2)(2x,[6x4x])(2x,4)\n" +
                        "(6x,2x)(2x,2)(2,[6x4])\n" +
                        "(2x,2)(2x,[6x4])(6,2x)\n" +
                        "(6x,2)(2x,2)(2,[6x4x])\n" +
                        "(6x,2)(2,2x)([6x4],2x)\n" +
                        "(2,4)([4x4],2x)\n" +
                        "(2,4x)([4x4],2)\n" +
                        "(2,2x)([6x6],0)\n" +
                        "(4,6x)(2x,2)(2x,[44x])\n" +
                        "(4,6)(2x,2)(2,[44x])\n" +
                        "(6x,4x)(2x,2)(2,[44x])\n" +
                        "(2,4)([4x4],6x)\n" +
                        "(2,4)([64x],4x)\n" +
                        "(2,4x)([4x4],6)\n" +
                        "(2,4x)([6x4],4)\n" +
                        "(2,6x)([6x4x],2x)\n" +
                        "(2,[4x4])([4x4],2)\n" +
                        "(2,4)([6x6],4)(6,2x)\n" +
                        "(2,4x)([8x6],4x)(2x,4)\n" +
                        "(2,6)([6x6],2x)(6x,2x)\n" +
                        "(4,2)(4,[6x4])*\n" +
                        "(4,2)(6,[4x4])*\n" +
                        "(4x,2)([64],4)*\n" +
                        "(6x,[4x4])(2,4x)*\n" +
                        "(2,4x)([8x8],4)(4,2x)(2x,6)\n" +
                        "([64],4)(4x,6)(4,2)*\n" +
                        "([64],4)(4x,6x)(4x,2)*\n" +
                        "(6x,[4x4])(6x,4x)(2,4x)*\n" +
                        "([6x4x],2x)(6,4x)(6x,2)*\n" +
                        "([6x6],4)(6,2x)(2,4x)*\n" +
                        "([6x6],4x)(2x,6x)(4x,2)*\n" +
                        "([6x4x],2)(6x,4x)(6x,2)*\n" +
                        "([6 4x],2x)(4x,6)(6x,2)*\n" +
                        "([6x4],2)(4,6x)(6,2)*\n" +
                        "([6x4],4)(6x,4x)(4,2)*\n" +
                        "(2,2)([64],6)\n" +
                        "(2,2)([6x6],4x)\n" +
                        "(2,2x)([64x],6)\n" +
                        "(2,2x)([6x6],4)\n" +
                        "(2,2x)([8x6x],2x)\n" +
                        "(2,2)(4,[6x6])*\n" +
                        "(2,2)(6,[64x])*\n" +
                        "(2,2)([64x],6)*\n" +
                        "(2,2x)([6x6],4x)*\n" +
                        "(2,2)(2x,[86])*\n" +
                        "(2,2x)([22],2x)([8x6x4x],2x)\n" +
                        "(2,2)([6,4],[6,4])\n" +
                        "(2,4)([8,6],4)\n" +
                        "(2,4x)([84x],6)\n" +
                        "(2,6)([64],6)\n" +
                        "(2,6)([6x6],4x)\n" +
                        "(2,6x)([6x6],4)\n" +
                        "(2,8x)([6x4],4)\n" +
                        "(2,4)([8x6],4x)\n" +
                        "(2,4x)([86x],4)\n" +
                        "(2,4)([8x8],2x)\n" +
                        "(2,4x)([8x8],2)";

                //TODO
                //      THE CURRENT PROBLEM:
                //  it takes so long to add all these siteswaps that the screen times out,
                //      for some reason the wakelock doesnt keep it from happening either,
                //      i put a toast in at the begining of this function and it doesnt even run, it
                //      just skips over it. IT works right now, i just have to keep touching
                //      the screen until it finishes doing the siteswap adding

                String[] originalInputArray =
                        bigListOfSiteswaps.split("[\\r\\n]+");

                int loadIndicatorInt = 0;

                    for (int i = 0; i < originalInputArray.length; i++) {

                        //and check to see if each one is a valid siteswap
                        //numberOfObjectsInSiteswap(originalInputArray[i]);
                        //I am not sure if we need to turn / and \ into blanks, maybe this is not needed, but
                        //      it really does not matter i think. also, i am not sure if \\ is being taken as
                        //      \ but i kind of think it might be.
                        String siteswapName = originalInputArray[i].replace(" ", "").replace("/", "").replace("\\", "");



                                //we make the entry name by following this format @@##&&name@@##&&number@@##&&type@@##&&

                                //the name = ss:THE_SITESWAP

                                int numberOfObjs = numberOfObjectsInSiteswap(originalInputArray[i]);

                                //we figure out the number based on the siteswap
                                String siteswapNumberOfObjects =
                                        Integer.toString(numberOfObjectsInSiteswap(originalInputArray[i]));
                                // since it is valid, we are going to add a new entry

                                if (numberOfObjs <= Integer.parseInt(getCellFromDB("SETTINGS", "MISC", "ID", "1")) &&
                                        numberOfObjs > 0) {



                                    String[] listOfObjTypes = new String[] {"Balls","Beanbags","Clubs","Rings"};

                                    for (int j = 0; j < 4; j++) {

                                        String siteswapEntryNameToAdd = buffer + "ss:" + siteswapName +
                                                buffer + siteswapNumberOfObjects +
                                                buffer + listOfObjTypes[j] + buffer;

                                        //debugging
                                        //Toast.makeText(MainActivity.this, "entryName = "+siteswapEntryNameToAdd, Toast.LENGTH_LONG).show();

                                        //if it does not have an name empty, and if it is already in the
                                        //          database, then we add it to the database
                                        if (isValidInput(siteswapName) && !containsCaseInsensitive
                                                (siteswapEntryNameToAdd, getColumnFromDB("PATTERNS", "ENTRYNAME"))) {

                                            //now we add our new siteswap pattern to the DB in the 'PATTERNS' table
                                            addDataToDB("PATTERNS", "ENTRYNAME", siteswapEntryNameToAdd);

                                            myDb.updateData("PATTERNS", "LINK", "ENTRYNAME", siteswapEntryNameToAdd,
                                                    "www.siteswapbot.com/" + siteswapName);

                                            myDb.updateData("PATTERNS", "SS", "ENTRYNAME", siteswapEntryNameToAdd, siteswapName);

                                            //I AM NOT SURE IF THIS IS NEEDED OR NOT
                                            //clearAddPatternInputs();

                                        }
                                    }
                                }



                    }
                //since we just added a new pattern to the DB, we want to update the mainPatternInputs with it
                //Log.d("z", "8");
                fillPatternMainInputsFromDB();

                    patternsATV.setText("ss:" + originalInputArray[0]);
                    setSpinnerSelection(numOfObjsSpinner, Integer.toString(numberOfObjectsInSiteswap(originalInputArray[0])));
                    setSpinnerSelection(objTypeSpinner, "Balls");

                updateAddPatFromPatternsInputsSelected();


                //wakeLock.release();

            }
        });



        alertBuilder.setCancelable(true)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        Dialog dialog = alertBuilder.create();
        dialog.show();

    }

    public void showNotesDialog() {
        View view = (LayoutInflater.from(MainActivity.this)).inflate(R.layout.notesdialog, null);

        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(MainActivity.this);
        alertBuilder.setView(view)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Notes!\n");

        TextView settingsNotesTV = (TextView) view.findViewById(R.id.settingsNotesTV);
        settingsNotesTV.setText("Use the area below to keep notes.");

        final EditText settingNotesEditText = (EditText) view.findViewById(R.id.settingNotesEditText);
        settingNotesEditText.setText(getCellFromDB("SETTINGS", "MISC", "ID", "11"));
        //settingNotesEditText.setText("test");


        alertBuilder.setCancelable(true)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        updateDataInDB("SETTINGS", "MISC", "ID", "11", settingNotesEditText.getText().toString());
                    }
                });
        Dialog dialog = alertBuilder.create();
        dialog.show();

    }

    public void onPause() {
        super.onPause();

        View view = getCurrentFocus();

        numOfObjsSpinner.requestFocus();
        patternsATV.requestFocus();
        view.requestFocus();


    }

    @Override
    protected void onStop() {
        super.onStop();  // Always call the superclass method first
        //this just makes sure we turn off the wakeLock when the app closes
        if (wakeLock.isHeld()) {
            wakeLock.release();
        }
    }
    //--------------------------BEGIN DIRECTLY USED BY onCreate()----------------------------------

    public void addFirstTimeRunDatabaseData() {


        //this is for settings and such that need to not be empty before they get set by the user

        // use this format to access these: getCellFromDB("SETTINGS","MISC","ID",id);
        //since these use 'add' they are being added to the DB in order and that is what is determining their id#
        Log.d("firstTime", "addData1");

        addDataToDB("SETTINGS", "MISC", "11"); //max number of props (id = 1)
        addDataToDB("SETTINGS", "MISC", "Balls,Beanbags,Clubs,Rings"); //prop types (id = 2)
        addDataToDB("SETTINGS", "MISC", "on"); //audio that plays when a drill set is completed(on/off) (id = 3)
        addDataToDB("SETTINGS", "MISC", "on"); //keep screen on during runs(on/off) (id = 4)
        addDataToDB("SETTINGS", "MISC", "3"); //number of seconds before runs begin (id = 5)
        addDataToDB("SETTINGS", "MISC", "1"); //keeps track of the last selected number of objects in the main input for use next time
        //                                                          app is opened (id = 6)
        addDataToDB("SETTINGS", "MISC", "0"); //keeps track of the last selected object type in the main input for use next time
        //                                                          app is opened (id = 7)
        addDataToDB("SETTINGS", "MISC", "on"); //keeps track of the last selected object type in the main input for use next time
        //                                                          app is opened (id = 8)
        addDataToDB("SETTINGS", "MISC", "on"); //keeps track of whether or not intro screen should be shown (id = 9)
        //keeps track all the special throw sequences (id = 10)
        addDataToDB("SETTINGS", "MISC", "ny,nyy,nyn,nny,ynn,yyn,yny");
        addDataToDB("SETTINGS", "MISC", "these are the initial notes");

        Log.d("firstTime", "addData2");

        addPatternsToDatabase();

        //addBulkSiteswapButton.performClick();
    }

    public void addPatternsToDatabase(){
        //AFTER WE GET THIS CLASS ALL SORTED OUT TO PREFILL THE APP WITH SITESWAPS,
        //      WE NEED TO FIGURE OUT WHY THE STATS TAB IS NOT WORKING

        //first, we need to make a string[] of all the object types
        String[] objectTypes = new String[]{"Balls,Beanbags,Clubs,Rings"};
        //then, we need to make a string[] of all the siteswaps(we can also toss in 'freestyle' eventually for each number of objs)
        String[] listOfSiteswaps = new String[]{"a","b","c"};
        //we need a for loop that goes through each of our object types
        //then, we need a for loop that goes through each of our siteswaps
        //  inside this loop we need to use our function that determines the number of objects
        //  these 2 for loops will result in us having a unique siteswapEntryNameToAdd, it will have an obj type, num, and ss
        //then we need to run the code below to actually add the siteswapEntryNameToAdd to the DB





        //now we add our new siteswap pattern to the DB in the 'PATTERNS' table
/*       addDataToDB("PATTERNS", "ENTRYNAME", siteswapEntryNameToAdd);

        myDb.updateData("PATTERNS", "LINK", "ENTRYNAME", siteswapEntryNameToAdd,
                "www.siteswapbot.com/" + siteswapName);

        myDb.updateData("PATTERNS", "SS", "ENTRYNAME", siteswapEntryNameToAdd, siteswapName);
        numberOfSiteswapsAdded++;

        //since we just added a new pattern to the DB, we want to update the mainPatternInputs with it
        //Log.d("z", "8");
        fillPatternMainInputsFromDB();*/
    }

    public void setupMainInputs() {

        //this makes the spinner that is used to choose how many objects are used in this pattern
        numOfObjsSpinner = (Spinner) findViewById(R.id.numberOfObjectsSpinner);
        ArrayAdapter<String> numOfObjsSpinnerAdapter = new ArrayAdapter<>(this, android.R.layout.select_dialog_item, addPatNumberOfObjectsForSpinner);
        numOfObjsSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        numOfObjsSpinner.setAdapter(numOfObjsSpinnerAdapter);
        numOfObjsSpinner.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showInfoDialog("Number of objects", "This is the main input for the number of objects you wish to work with. The max number" +
                        " of objects can be set it in the 'SET' tab. Number of objects are used in conjunction with the object type and" +
                        " the pattern name to create a unique entry in the database.");
                return true;
            }
        });

        numOfObjsSpinner.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                updateDBfromAddInputs(); //since we are changing the number of objects we will be leaving the currently
                //                                  selected entry, so we save everything to it


                return false;
            }
        });


        numOfObjsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                histNumberOfObjectsOtherThanCurrentCheckbox.setText("Number of objects other than '" + numOfObjsSpinner.getSelectedItem().toString() + "'");
                fillHistoryRecordsTextView();

                //Toast.makeText(getBaseContext(), parent.getItemAtPosition(position) + " selected", Toast.LENGTH_LONG).show();
                updateAddPatFromPatternsInputsSelected();
                //Log.d("z", "2");
                fillPatternMainInputsFromDB();

                //a 'num of objects' has just been selected, so there may be a new currentEntry and we must update
                //      what is currently being shown on the hitory tab activity
                // loadHistoryRecordsFromDB();

                //this keeps track of which obj num is selected so that we are on it when we open the app again
                updateDataInDB("SETTINGS", "MISC", "ID", "6", Integer.toString(numOfObjsSpinner.getSelectedItemPosition()));

                //debugging
                //Toast.makeText(getBaseContext(), getCellFromDB("SETTINGS", "MISC", "ID", "6"), Toast.LENGTH_LONG).show();


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

//        //sets the current number of objects based on what was saved to the settings DB last time a
//        //      number was selected
//        numOfObjsSpinner.setSelection(Integer.parseInt(getCellFromDB("SETTINGS","MISC","ID","6")));


        //this makes the spinner that is used to choose how many objects are used in this pattern
        objTypeSpinner = (Spinner) findViewById(R.id.objectTypeSpinner);
        ArrayAdapter<String> objTypeSpinnerAdapter = new ArrayAdapter<>(this, android.R.layout.select_dialog_item, addPatObjTypeForSpinner);
        objTypeSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        objTypeSpinner.setAdapter(objTypeSpinnerAdapter);
        objTypeSpinner.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showInfoDialog("Prop type", "This is the main input for the prop type. The prop types available here can be set" +
                        " it in the 'SET' tab. Prop types are used in conjunction with the number of props and the pattern name to create" +
                        " a unique entry in the database.");
                return true;
            }
        });

        objTypeSpinner.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                updateDBfromAddInputs(); //since we are changing the number of objects we will be leaving the currently
                //                                  selected entry, so we save everything to it
                return false;
            }
        });

        objTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                histObjectTypesOtherThanCurrentCheckbox.setText("Object types other than '" + objTypeSpinner.getSelectedItem().toString() + "'");
                fillHistoryRecordsTextView();
                //THIS SEEMS REDUNDANT SINCE IT IS JUST ABOVE IN 'objTypeSpinner.setOnTouchListener'
                //      FURTHERMORE, THIS IS NOT HOW 'numOfObjs' IS SET UP ABOVE, AND I THINK THYE SHOULD BE THE SAME
                //updateDBfromAddInputs(); //since we are changing the number of objects we will be leaving the currently
                //                                  selected entry, so we save everything to it

                //Toast.makeText(getBaseContext(), parent.getItemAtPosition(position) + " selected", Toast.LENGTH_LONG).show();
                updateAddPatFromPatternsInputsSelected();
                //Log.d("z", "3");
                //fillPatternMainInputsFromDB();

                //a 'num of objects' has just been selected, so there may be a new currentEntry and we must update
                //      what is currently being shown on the hitory tab activity
                // loadHistoryRecordsFromDB();

                //save the currently selected item to the settings database to keep it in memory to be the selected item
                //      when the app is started next time
                updateDataInDB("SETTINGS", "MISC", "ID", "7", Integer.toString(objTypeSpinner.getSelectedItemPosition()));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        //connect our autocompleteTextView coniables to their counterparts in the layout
        patternsATV = (AutoCompleteTextView) findViewById(R.id.patternATV);
        //make our adapters(the text/arrays) which fill our patterns/Modifiers autocompletTextVies
        ArrayAdapter patternAdapter = new ArrayAdapter(this, android.R.layout.select_dialog_item, patterns);
        patternsATV.setAdapter(patternAdapter);//hook the appropriate adapter from above to the ATV
        patternsATV.setThreshold(0);//this is number of letters that must match for autocomplete to suggest a word
        //the onFocusChange and onClick Listeners are used to make the unintentionalEndDown list appear just from tapping
        //      the autocompleteTextView
        patternsATV.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                    showInfoDialog("Patterns", "This is the main input for the pattern name. The pattern name is used in conjunction with the number" +
                            " of objects and the prop type to create a unique entry. To record a run with this entry, use the 'JUG' tab, to" +
                            " view a record of all past runs with this entry, use the 'HIS' tab, and to create new patterns, or edit existing " +
                            " patterns, use the 'ADD' tab. To see a list of patterns that you have previously completed runs with, click " +
                            "'Patterns with history'.");
                    return true;

            }
        });
        patternsATV.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    patternsATV.showDropDown();
                    //Toast.makeText(MainActivity.this, "patFocus", Toast.LENGTH_LONG).show();
                } else {
                    //if the 'DB list of patterns' doesn't include the pattern just input, then
                    //          make the patternATV be blank
                    if (!containsCaseInsensitive(patternsATV.getText().toString(), patterns)) {
                        patternsATV.setText("");
                    }

                    if (!patternAddTabCurrentlyUpdatedWith.equals((patternsATV.getText().toString()))) {
                        updateAddPatFromPatternsInputsSelected();
                    }
                    //Log.d("z", "4");
                    fillPatternMainInputsFromDB();
                    histPatternNamesOtherThanCurrentCheckbox.setText("Pattern names other than '" + patternsATV.getEditableText().toString() + "'");
                    fillHistoryRecordsTextView();
                    //a 'patternATV'(pattern name) has just changed focus, so there may be a new currentEntry and we must update
                    //      what is currently being shown on the hitory tab activity
                    // loadHistoryRecordsFromDB();

                }
            }
        });
        patternsATV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(MainActivity.this, "patClicked", Toast.LENGTH_LONG).show();
                patternsATV.showDropDown();
            }
        });
        patternsATV.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View arg1, int pos,
                                    long id) {
                // Toast.makeText(MainActivity.this," selected", Toast.LENGTH_LONG).show();

                //i think we don't want this here, it is just for modifiers
                //updateModifierMATV();


                updateAddPatFromPatternsInputsSelected();
                //Log.d("z", "5");
                fillPatternMainInputsFromDB();

                //a 'patternATV'(pattern name) has just been clicked on, so there may be a new currentEntry and we must update
                //      what is currently being shown on the hitory tab activity
                // loadHistoryRecordsFromDB();

                hideKeyboard();

                histPatternNamesOtherThanCurrentCheckbox.setText("Pattern names other than '" + patternsATV.getEditableText().toString() + "'");
                fillHistoryRecordsTextView();
            }
        });

        modifierMATV = (MultiAutoCompleteTextView) findViewById(R.id.modifierMATV);
        ArrayAdapter<String> modifierAdapter = new ArrayAdapter<>(this, android.R.layout.select_dialog_item, modifiers);
        modifierMATV.setAdapter(modifierAdapter);//hook the appropriate adapter from above to the ATV
        modifierMATV.setThreshold(0);//this is number of letters that must match for autocomplete to suggest a word
        modifierMATV.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());//gives it a comma between each selection
        //the onFocusChange and onClick Listeners are used to make the unintentionalEndDown list appear just from tapping
        //      the multiAutocompleteTextView
        modifierMATV.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showInfoDialog("Modifiers", "This is the main input for modifiers. For more information on modifiers, hold down on the large " +
                        "'MODIFIERS' header in the 'ADD' tab.");
                return true;
            }
        });
        modifierMATV.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                //modifierMATV.showDropDown();
                //every time we lose the focus, we want to update the addModifierNamesSpinner with what is in it

                if (hasFocus) {
                    updateDBfromAddInputs();
                    modifierMATV.showDropDown();
                } else {

                    updateDBfromAddInputs();
                    updateModifierMATV();
                    updateAddModifierFromModifierMATVselected();

                    histModifierCombinationOtherThanExactlyCurrentCheckbox.setText("Modifier combination other than exactly '" + modifierMATV.getEditableText().toString() + "'");
                    histModifierCombinationThatDoesntContainCurrentCheckbox.setText("Modifier combination that doesn't contain '" + modifierMATV.getEditableText().toString() + "'");
                    fillHistoryRecordsTextView();

                    //a 'modifierMATV'(modifier combo) has just changed focus, so there may be a new currentEntry and we must update
                    //      what is currently being shown on the hitory tab activity
                    // loadHistoryRecordsFromDB();

                    //  modifierMATV.showDropDown();
                }


//                if (hasFocus) {
//                    modifierMATV.showDropDown();
//                    Toast.makeText(MainActivity.this, "conHasFocus", Toast.LENGTH_LONG).show();
//                }else{
//                    Toast.makeText(MainActivity.this, "conNoFocus", Toast.LENGTH_LONG).show();
//                    updateModifierMATV();
//                }

            }
        });
        modifierMATV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                modifierMATV.showDropDown();
                histModifierCombinationOtherThanExactlyCurrentCheckbox.setText("Modifier combination other than exactly '" + modifierMATV.getEditableText().toString() + "'");
                histModifierCombinationThatDoesntContainCurrentCheckbox.setText("Modifier combination that doesn't contain '" + modifierMATV.getEditableText().toString() + "'");
                fillHistoryRecordsTextView();
            }
        });


//        modifierMATV.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                modifierMATV.showDropDown();
//                return false;
//            }
//        });

        modifierMATV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                updateModifierMATV();
                updateAddModifierFromModifierMATVselected();
                histModifierCombinationOtherThanExactlyCurrentCheckbox.setText("Modifier combination other than exactly '" + modifierMATV.getEditableText().toString() + "'");
                histModifierCombinationThatDoesntContainCurrentCheckbox.setText("Modifier combination that doesn't contain '" + modifierMATV.getEditableText().toString() + "'");
                fillHistoryRecordsTextView();

                //a 'modifierMATV'(modifier combo) has just been clicked on, so there may be a new currentEntry and we must update
                //      what is currently being shown on the hitory tab activity
                // loadHistoryRecordsFromDB();
            }
        });
        modifierMATV.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                updateModifierMATV();
                updateAddModifierFromModifierMATVselected();
                histModifierCombinationOtherThanExactlyCurrentCheckbox.setText("Modifier combination other than exactly '" + modifierMATV.getEditableText().toString() + "'");
                histModifierCombinationThatDoesntContainCurrentCheckbox.setText("Modifier combination that doesn't contain '" + modifierMATV.getEditableText().toString() + "'");
                fillHistoryRecordsTextView();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        mainModifiersWithPastButton = (Button) findViewById(R.id.mainModifiersWithPastButton);
        mainModifiersWithPastButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //when this is clicked, we open a dialog that has a spinner on it, but we only do that if there is a valid
                //      pattern name in the main paternATV input, this try intentionalEnd checks to see if it is valid or not

                randomlySelectMainInputs();



            }
        });
        mainModifiersWithPastButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                //THIS LONGHOLD NEEDS A CUSTOM DIALOG THAT HAS THESE BUTTONS:
                //  -past modifiers with current pattern
                //       -when this button is clicked we want to run this code:

                //  -patterns with history
                //  -settings for randomizer
                //      -these settings need to affect how this function works - randomlySelectMainInputs();


                showRandomizerDialog();
                return true;
            }
        });


    }

    public void showRandomizerDialog(){

        new AlertDialog.Builder(MainActivity.this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Randomizer")
                .setMessage("Clicking this button will randomly set the main inputs based on the conditions set in the " +
                        "randomizer settings found below. To see a list of all patterns with history with the currently selected" +
                        " object number and prop type, click 'patterns with history'.  To see a list of all modifier combinations " +
                        "with history with the currently selected pattern, click 'modifiers with history'")
                .setPositiveButton("Randomizer settings", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        showRandomizerSettingsDialog();
                    }
                })
                .setNeutralButton("Modifiers with history", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                Boolean isValidPattern = true;
                try {
                    getColumnFromDB("HISTORYENDURANCE", currentEntryName());
                } catch (Exception e) {
                    Toast.makeText(MainActivity.this, "There are no past modifiers with the current pattern.", Toast.LENGTH_LONG).show();
                    isValidPattern = false;
                }
                if (isValidPattern) {
                    showMainModifiersWithPastDialog();
                }
                    }
                })
                .setNegativeButton("Patterns with history", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        showPatternsWithHistoryDialog();
                    }
                })
                .show();
    }

    public void showRandomizerSettingsDialog(){//todo, this is just being used to mark this spot


            View view = (LayoutInflater.from(MainActivity.this)).inflate(R.layout.randomizersettingsdialog, null);

            AlertDialog.Builder alertBuilder = new AlertDialog.Builder(MainActivity.this);
            alertBuilder.setView(view);



            alertBuilder.setCancelable(true)
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }


                    });
            alertBuilder.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                }
            });
            Dialog dialog = alertBuilder.create();

            dialog.show();
    }

    public void randomlySelectMainInputs() {

        //todo
        //  -we want to randomly select a pattern based on the settings, maybe even an obj num or prop type,
        //  -the current system isnt smooth, it has a couple issues,
        //          1) it doesn't know if the checkboxes have been checked or not because they reset every time the
        //      dialog opens
        //          2) we need a better way to randomly pick a pattern with a history or without a history or both
        //  -we can save settings directly from the dialog to the db and then check the db later when we
        //      need to see what kind of randomizer stuff we want
        //          -randomizerSelectingPatternWithHistory
        //          -randomizerSelectingPatternWithoutHistory
        //          -randomizerSelectingObjectNumber
        //          -randomizerSelectingPropType

        //THIS IS NOT THE PLACE FOR THIS STUFF, WE WANT TO DO ALL CHECKBOX
        //      STUFF IN THE RANDOMIZER SETTINGS DIALOG WHEN IT SHOWS.
//        View view = (LayoutInflater.from(MainActivity.this)).inflate(R.layout.randomizersettingsdialog, null);
//
//
//        CheckBox randSetObjNum = (CheckBox) view.findViewById(R.id.randSetObjNum);
//        CheckBox randSetPropType = (CheckBox) view.findViewById(R.id.randSetPropType);
//
//        CheckBox randSetWithHist = (CheckBox) view.findViewById(R.id.randSetWithHist);
//        CheckBox randSetWithoutHist = (CheckBox) view.findViewById(R.id.randSetWithoutHist);

        //todo, once we have the settings set up to keep track of these booleans, then we can uncomment and
        //      fill in the stuff below here
//        if(randomizerSelectingObjectNumber){
//            //select an object number
//        }
//        if(randomizerSelectingPropType){
//            //select a prop type
//        }

            patternsATV.setText(randomizerChoosePattern());




                //since we just selected a random pattern, we update the history tab with it
        histPatternNamesOtherThanCurrentCheckbox.setText("Pattern names other than '" + patternsATV.getEditableText().toString() + "'");
        fillHistoryRecordsTextView();

    }

    public String randomizerChoosePattern(){

        int numberOfPatterns = myDb.getFromDB("HISTORYENDURANCE").getColumnCount();

//        final Spinner patternsWithHistorySortSpinner = (Spinner) view.findViewById(R.id.patternsWithHistorySortSpinner);
//        List<String> patternsWithHistorySortSpinnerList = new ArrayList<>(); //our list of patterns
//        patternsWithHistorySortSpinnerList.add(0,"0");
//        ArrayAdapter<String> patternsWithHistorySortSpinnerAdapter = new ArrayAdapter<>(MainActivity.this,
//                android.R.layout.select_dialog_item, patternsWithHistorySortSpinnerList);
//        patternsWithHistorySortSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        patternsWithHistorySortSpinner.setAdapter(patternsWithHistorySortSpinnerAdapter);
//

        //based on the position of the selected spinner item, we need to fill our list, patternsWithHistoryPatternsSpinnerList,
        //  as well as add the secondary information to it if it has any

        //todo, these booleans are temporary, we need to make the checkboxes hooked up to setting s that are kept track
        //      of in the DB, this is going to meant that we must wipe the DB and reinstall probably
Boolean randomizerSelectingPatternWithHistory = true;
Boolean randomizerSelectingPatternWithoutHistory = false;
        List<String> patternsWithHistoryPatterns = new ArrayList<>();

            //Log.d("sortPattern", "Alphabetical");
            //the reason we start at 2 is because 0 is the id and 1 is the 'modifiers column, we just want 2 and beyond
            for (int i = 2; i < numberOfPatterns; i++) {

                //we only want to add pattern names that have the same number of objs and prop type that is currently
                //      selected in the main inputs
                ////Log.d("checkHereA", myDb.getFromDB("HISTORYENDURANCE").getColumnName(i).split(buffer)[2]);
                ////Log.d("checkHereB", numOfObjsSpinner.getSelectedItem().toString());
                if (myDb.getFromDB("HISTORYENDURANCE").getColumnName(i).split(buffer)[2].equals(numOfObjsSpinner.getSelectedItem().toString()) &&
                        myDb.getFromDB("HISTORYENDURANCE").getColumnName(i).split(buffer)[3].equals(objTypeSpinner.getSelectedItem().toString())) {
                    //Log.d("checkHereC", Integer.toString(i));
                    if (entryInfo(myDb.getFromDB("HISTORYENDURANCE").getColumnName(i), "initialDate").equals("01/01/31 01:01:01") ||
                            entryInfo(myDb.getFromDB("HISTORYENDURANCE").getColumnName(i), "initialDate").equals("01/01/01 01:01:01")) {
                        //Log.d("rejectedA", myDb.getFromDB("HISTORYENDURANCE").getColumnName(i));
                        if(randomizerSelectingPatternWithoutHistory) {
                            patternsWithHistoryPatterns.add(myDb.getFromDB("HISTORYENDURANCE").getColumnName(i).split(buffer)[1]);
                        }
                    } else {
                        if(randomizerSelectingPatternWithHistory) {
                            patternsWithHistoryPatterns.add(myDb.getFromDB("HISTORYENDURANCE").getColumnName(i).split(buffer)[1]);
                        }
                    }

                }
                ////Log.d("sortPattern", patternsWithHistoryPatterns.get(i-2));
            }







        //this chooses a random pattern
        //int numberOfPatterns = patterns.size();
        if (patternsWithHistoryPatterns.size() > 0) {
            Random rand = new Random();
            int n = rand.nextInt(patternsWithHistoryPatterns.size());
            return patternsWithHistoryPatterns.get(n);
        }
        else{
            return "";
        }

    }


    public void showMainModifiersWithPastDialog() {

        fillListOfModifiersWithPastWithSelectedPattern();

        //we only want to show the dialog of past modifiers if there are past modifiers to show, so if the size of the
        //      list of past modifiers is 0, then we just tell the user that there are none instead.
        if (listOfModifiersWithPastWithSelectedPattern.size() == 0) {
            Toast.makeText(MainActivity.this, "The selected pattern has no past modifiers.", Toast.LENGTH_LONG).show();
        } else {

            View view = (LayoutInflater.from(MainActivity.this)).inflate(R.layout.mainmodifierswithpastdialog, null);

            AlertDialog.Builder alertBuilder = new AlertDialog.Builder(MainActivity.this);
            alertBuilder.setView(view);

            //final TextView reasonForIntentionalEndTV = (TextView) view.findViewById(R.id.reasonForIntentionalEndTV);

            final Spinner mainModifiersWithPastSpinner =
                    (Spinner) view.findViewById(R.id.mainModifiersWithPastSpinner);
            ArrayAdapter<String> mainModifersWithPastSpinnerAdapter =
                    new ArrayAdapter<>(MainActivity.this, android.R.layout.select_dialog_item, listOfModifiersWithPastWithSelectedPattern);
            mainModifiersWithPastSpinner.setAdapter(mainModifersWithPastSpinnerAdapter);


            alertBuilder.setCancelable(true)
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //when ok is clicked, we take the currently selected modifiers in our spinner,
                            //  and set them as the seleceted modifiers in the
                            modifierMATV.setText(mainModifiersWithPastSpinner.getSelectedItem().toString());


                        }


                    });
            alertBuilder.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                }
            });
            Dialog dialog = alertBuilder.create();

            dialog.show();
        }
    }

    public void fillListOfModifiersWithPastWithSelectedPattern() {
        List<String> listOfEveryHistoryItemForCurrentEntry = new ArrayList<>();
        List<String> listOfModifiersWithPastWithAnyPattern = new ArrayList<>();
        listOfEveryHistoryItemForCurrentEntry = getColumnFromDB("HISTORYENDURANCE", currentEntryName());
        listOfModifiersWithPastWithAnyPattern = getColumnFromDB("HISTORYENDURANCE", "MODIFIERS");
        //listOfAllModifiersWithPastWithAnyPatterns.addAll(getColumnFromDB("HISTORYDRILL","MODIFIERS"));
        //first we do endurance,
        for (int i = 0; i < listOfEveryHistoryItemForCurrentEntry.size(); i++) {
            if (listOfEveryHistoryItemForCurrentEntry.get(i) != null && !listOfEveryHistoryItemForCurrentEntry.get(i).isEmpty()) {
                listOfModifiersWithPastWithSelectedPattern.add(listOfModifiersWithPastWithAnyPattern.get(i));
            }
        }
        //then we do drill
        listOfEveryHistoryItemForCurrentEntry = getColumnFromDB("HISTORYDRILL", currentEntryName());
        listOfModifiersWithPastWithAnyPattern = getColumnFromDB("HISTORYDRILL", "MODIFIERS");
        //listOfAllModifiersWithPastWithAnyPatterns.addAll(getColumnFromDB("HISTORYDRILL","MODIFIERS"));
        //first we do endurance,
        for (int i = 0; i < listOfEveryHistoryItemForCurrentEntry.size(); i++) {
            if (listOfEveryHistoryItemForCurrentEntry.get(i) != null && !listOfEveryHistoryItemForCurrentEntry.get(i).isEmpty()) {
                listOfModifiersWithPastWithSelectedPattern.add(listOfModifiersWithPastWithAnyPattern.get(i));
            }
        }

        //then we remove duplicates
        listOfModifiersWithPastWithSelectedPattern = removeDuplicatesFromList(listOfModifiersWithPastWithSelectedPattern);
        Collections.sort(listOfModifiersWithPastWithSelectedPattern, String.CASE_INSENSITIVE_ORDER);

    }

    public void setupStats() {
        //STANDARD STATS ORDER: (13 in all)
        //  -runs
        //  -endur runs
        //  -endur runs PB
        //  -endur runs intentionalEnd
        //  -endur runs intentionalEnd PB
        //  -endur runs unintentionalEnd
        //  -endur runs intentionalEnd PB
        //  -init endur runs
        //  -drill runs
        //  -drill runs restart
        //  -drill runs restart dropless
        //  -drill runs don't restart
        //  -init drill runs


        ScrollView sv = (ScrollView) findViewById(R.id.layout5);
        sv.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                clearFocusOfAllEditTexts();
                // this hides the keyboard if the scrollview is touched
                hideKeyboard();

                return false;
            }
        });


        statsTotalNumberOfLabel = (TextView) findViewById(R.id.statsTotalNumberOfLabel);

        ////Log.d("myTag", "1");
        statsTotalNumberOfLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                List<TextView> textViewList = new ArrayList<TextView>();
                textViewList.add(statsExtenderForSeperators);
                textViewList.add(statsTotalRunsLabel);
                textViewList.add(statsTotalRunsNum);
                textViewList.add(statsEndurRunsLabel);
                textViewList.add(statsEndurRunsNum);
                textViewList.add(statsEndurPersonalBestLabel);
                textViewList.add(statsEndurPersonalBestNum);
                textViewList.add(statsEndurRunsIntentionalEndLabel);
                textViewList.add(statsEndurRunsIntentionalEndNum);
                textViewList.add(statsEndurRunsIntentionalEndPersonalBestLabel);
                textViewList.add(statsEndurRunsIntentionalEndPersonalBestNum);
                textViewList.add(statsEndurRunsUnintentionalEndLabel);
                textViewList.add(statsEndurRunsUnintentionalEndNum);
                textViewList.add(statsEndurRunsUnintentionalEndPersonalBestLabel);
                textViewList.add(statsEndurRunsUnintentionalEndPersonalBestNum);
                textViewList.add(statsInitialEndurRunsLabel);
                textViewList.add(statsInitialEndurRunsNum);
                textViewList.add(statsDrillRunsLabel);
                textViewList.add(statsDrillRunsNum);
                textViewList.add(statsDrillRunsRestartDropLabel);
                textViewList.add(statsDrillRunsRestartNum);
                textViewList.add(statsDrillRunsRestartDropDroplessLabel);
                textViewList.add(statsDrillRunsRestartDroplessNum);
                textViewList.add(statsDrillRunsDontRestartDropLabel);
                textViewList.add(statsDrillRunsDontRestartNum);
                textViewList.add(statsInitialDrillRunsLabel);
                textViewList.add(statsInitialDrillRunsNum);
                textViewList.add(statsTotalEndSpacer);

                statsToggleTextViewVisibility(statsTotalNumberOfLabel, textViewList);


            }
        });
        statsTotalNumberOfLabel.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showInfoDialog("Total number of...", "The numbers in this section represent a complete count of all history items of each " +
                        "type indicated.");
                return true;
            }
        });
        statsExtenderForSeperators = (TextView) findViewById(R.id.statsExtenderForSeperators);
        statsTotalRunsLabel = (TextView) findViewById(R.id.statsTotalRunsLabel);
        statsTotalRunsLabel.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showInfoDialog("Total number of...", "...runs:\n\n" +
                        "A count of every run in your history.");
                return true;
            }
        });
        statsTotalRunsNum = (TextView) findViewById(R.id.statsTotalRunsNum);
        statsEndurRunsLabel = (TextView) findViewById(R.id.statsEndurRunsLabel);
        statsEndurRunsLabel.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showInfoDialog("Total number of...", "... endurance runs:\n\n" +
                        "A count of every endurance run in your history.");
                return true;
            }
        });
        statsEndurRunsNum = (TextView) findViewById(R.id.statsEndurRunsNum);
        statsEndurPersonalBestLabel = (TextView) findViewById(R.id.statsEndurPersonalBestLabel);
        statsEndurPersonalBestLabel.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showInfoDialog("Total number of...", "... endurance runs where a personal best was broken:\n\n" +
                        "A count of every endurance run in your history where you juggled for longer than all previous endurance runs with the exact same pattern specifics & modifiers" +
                        " where you also ended the same way, with either a intentionalEnd or a unintentionalEnd.");
                return true;
            }
        });
        statsEndurPersonalBestNum = (TextView) findViewById(R.id.statsEndurPersonalBestNum);
        statsEndurRunsIntentionalEndLabel = (TextView) findViewById(R.id.statsEndurRunsIntentionalEndLabel);
        statsEndurRunsIntentionalEndLabel.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showInfoDialog("Total number of...", "...endurance runs that ended intentionally:\n\n" +
                        "A count of every endurance run in your history that you ended intentionally.");
                return true;
            }
        });
        statsEndurRunsIntentionalEndNum = (TextView) findViewById(R.id.statsEndurRunsIntentionalEndNum);
        statsEndurRunsIntentionalEndPersonalBestLabel = (TextView) findViewById(R.id.statsEndurRunsIntentionalEndPersonalBestLabel);
        statsEndurRunsIntentionalEndPersonalBestLabel.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showInfoDialog("Total number of...", "...endurance runs that ended intentionally where a personal best was broken:\n\n" +
                        "A count of every endurance run in your history that ended intentionally in which you juggled for longer than all previous endurance " +
                        "runs of the exact same pattern specifics & modifiers.");
                return true;
            }
        });
        statsEndurRunsIntentionalEndPersonalBestNum = (TextView) findViewById(R.id.statsEndurRunsIntentionalEndPersonalBestNum);
        statsEndurRunsUnintentionalEndLabel = (TextView) findViewById(R.id.statsEndurRunsUnintentionalEndLabel);
        statsEndurRunsUnintentionalEndLabel.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showInfoDialog("Total number of...", "...endurance runs that ended unintentionally:\n\n" +
                        "A count of every endurance run in your history that you ended unintentionally.");
                return true;
            }
        });
        statsEndurRunsUnintentionalEndNum = (TextView) findViewById(R.id.statsEndurRunsUnintentionalEndNum);
        statsEndurRunsUnintentionalEndPersonalBestLabel = (TextView) findViewById(R.id.statsEndurRunsUnintentionalEndPersonalBestLabel);
        statsEndurRunsUnintentionalEndPersonalBestLabel.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showInfoDialog("Total number of...", "...endurance runs that ended unintentionally where a personal best was broken:\n\n" +
                        "A count of every endurance run in your history that ended unintentionally in which you juggled for longer than all previous endurance " +
                        "runs of the exact same pattern specifics & modifiers.");
                return true;
            }
        });
        statsEndurRunsUnintentionalEndPersonalBestNum = (TextView) findViewById(R.id.statsEndurRunsUnintentionalEndPersonalBestNum);
        statsInitialEndurRunsLabel = (TextView) findViewById(R.id.statsInitialEndurRunsLabel);
        statsInitialEndurRunsLabel.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showInfoDialog("Total number of...", "...initial endurance runs:\n\n" +
                        "A count of every endurance run in your history that has no other endurance runs which exist prior to it with the exact same pattern specifics and " +
                        "modifiers.");
                return true;
            }
        });
        statsInitialEndurRunsNum = (TextView) findViewById(R.id.statsInitialEndurRunsNum);
        statsDrillRunsLabel = (TextView) findViewById(R.id.statsDrillRunsLabel);
        statsDrillRunsLabel.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showInfoDialog("Total number of...", "...drill runs:\n\n" +
                        "A count of every drill run in your history.");
                return true;
            }
        });
        statsDrillRunsNum = (TextView) findViewById(R.id.statsDrillRunsNum);
        statsDrillRunsRestartDropLabel = (TextView) findViewById(R.id.statsDrillRunsRestartDropLabel);
        statsDrillRunsRestartDropLabel.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showInfoDialog("Total number of...", "...drill runs in \"restart set on unintentionalEnds\" mode:\n\n" +
                        "A count of every drill run in your history in which the \"restart set on unintentionalEnds\" checkbox was checked.");
                return true;
            }
        });
        statsDrillRunsRestartNum = (TextView) findViewById(R.id.statsDrillRunsRestartNum);
        statsDrillRunsRestartDropDroplessLabel = (TextView) findViewById(R.id.statsDrillRunsRestartDropDroplessLabel);
        statsDrillRunsRestartDropDroplessLabel.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showInfoDialog("Total number of...", "...drill runs in \"restart set on unintentionalEnds\" mode that were dropless:\n\n" +
                        "A count of every drill run in your history in which the \"restart set on unintentionalEnds\" checkbox was checked, but you didn't unintentionalEnd a single time.");
                return true;
            }
        });
        statsDrillRunsRestartDroplessNum = (TextView) findViewById(R.id.statsDrillRunsRestartDroplessNum);
        statsDrillRunsDontRestartDropLabel = (TextView) findViewById(R.id.statsDrillRunsDontRestartDropLabel);
        statsDrillRunsDontRestartDropLabel.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showInfoDialog("Total number of...", "...drill runs in \"don't restart set on unintentionalEnds\" mode:\n\n" +
                        "A count of every drill run in your history in which the \"restart set on unintentionalEnds\" checkbox was NOT checked.");
                return true;
            }
        });
        statsDrillRunsDontRestartNum = (TextView) findViewById(R.id.statsDrillRunsDontRestartNum);
        statsInitialDrillRunsLabel = (TextView) findViewById(R.id.statsInitialDrillRunsLabel);
        statsInitialDrillRunsLabel.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showInfoDialog("Total number of...", "...initial drill runs:\n\n" +
                        "A count of every drill run in your history that has no other drill runs which exist prior to it with the exact same pattern specifics and " +
                        "modifiers.");
                return true;
            }
        });
        statsInitialDrillRunsNum = (TextView) findViewById(R.id.statsInitialDrillRunsNum);
        statsTotalEndSpacer = (TextView) findViewById(R.id.statsTotalEndSpacer);

        statsTotalTimeLabel = (TextView) findViewById(R.id.statsTotalTimeLabel);
        statsTotalTimeLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                List<TextView> textViewList = new ArrayList<TextView>();
                textViewList.add(statsTotalTimeRunsLabel);
                textViewList.add(statsTotalTimeRunsNum);
                textViewList.add(statsTotalTimeEnduranceLabel);
                textViewList.add(statsTotalTimeEnduranceNum);
                textViewList.add(statsTotalTimeEndurancePersonalBestLabel);
                textViewList.add(statsTotalTimeEndurancePersonalBestNum);
                textViewList.add(statsTotalTimeEnduranceIntentionalEndLabel);
                textViewList.add(statsTotalTimeEnduranceIntentionalEndNum);
                textViewList.add(statsTotalTimeEnduranceIntentionalEndPersonalBestLabel);
                textViewList.add(statsTotalTimeEnduranceIntentionalEndPersonalBestNum);
                textViewList.add(statsTotalTimeEnduranceUnintentionalEndLabel);
                textViewList.add(statsTotalTimeEnduranceUnintentionalEndNum);
                textViewList.add(statsTotalTimeEnduranceUnintentionalEndPersonalBestLabel);
                textViewList.add(statsTotalTimeEnduranceUnintentionalEndPersonalBestNum);
                textViewList.add(statsTotalTimeInitialEnduranceLabel);
                textViewList.add(statsTotalTimeInitialEnduranceNum);
                textViewList.add(statsTotalTimeDrillsLabel);
                textViewList.add(statsTotalTimeDrillsNum);
                textViewList.add(statsTotalTimeDrillsRestartLabel);
                textViewList.add(statsTotalTimeDrillsRestartNum);
                textViewList.add(statsTotalTimeDrillsRestartDroplessLabel);
                textViewList.add(statsTotalTimeDrillsRestartDroplessNum);
                textViewList.add(statsTotalTimeDrillsDontRestartLabel);
                textViewList.add(statsTotalTimeDrillsDontRestartNum);
                textViewList.add(statsTotalTimeInitialDrillLabel);
                textViewList.add(statsTotalTimeInitialDrillNum);
                textViewList.add(statsTotalTimeEndSpacer);

                statsToggleTextViewVisibility(statsTotalTimeLabel, textViewList);

            }
        });
        statsTotalTimeLabel.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showInfoDialog("Total spent in...", "This section shows the amount of time spent in runs of each type indicated. The format used is DDD:hh:mm:ss, " +
                        "or DAYS:HOURS:MINUTES:SECONDS");
                return true;
            }
        });
        statsTotalTimeRunsLabel = (TextView) findViewById(R.id.statsTotalTimeRunsLabel);
        statsTotalTimeRunsLabel.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showInfoDialog("Total time spent in...", "...all runs:\n\n" +
                        "The combined amount of time you spent on all runs.");
                return true;
            }
        });
        statsTotalTimeRunsNum = (TextView) findViewById(R.id.statsTotalTimeRunsNum);
        statsTotalTimeEnduranceLabel = (TextView) findViewById(R.id.statsTotalTimeEnduranceLabel);
        statsTotalTimeEnduranceLabel.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showInfoDialog("Total time spent in...", "...endurance runs:\n\n" +
                        "The combined amount of time you spent on all endurance runs.");
                return true;
            }
        });
        statsTotalTimeEnduranceNum = (TextView) findViewById(R.id.statsTotalTimeEnduranceNum);
        statsTotalTimeEndurancePersonalBestLabel = (TextView) findViewById(R.id.statsTotalTimeEndurancePersonalBestLabel);
        statsTotalTimeEndurancePersonalBestLabel.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showInfoDialog("Total time spent in...", "...endurance runs where a personal best was broken:\n\n" +
                        "The combined amount of time you spent on all endurance runs where you juggled for longer than" +
                        " all previous endurance runs with the exact same pattern specifics & modifiers" +
                        " where you also ended the same way, with either a intentionalEnd or a unintentionalEnd.");

                return true;
            }
        });
        statsTotalTimeEndurancePersonalBestNum = (TextView) findViewById(R.id.statsTotalTimeEndurancePersonalBestNum);
        statsTotalTimeEnduranceIntentionalEndLabel = (TextView) findViewById(R.id.statsTotalTimeEnduranceIntentionalEndLabel);
        statsTotalTimeEnduranceIntentionalEndLabel.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showInfoDialog("Total time spent in...", "...endurance runs that ended intentionally:\n\n" +
                        "The combined amount of time you spent on all endurance runs which ended intentionally.");

                return true;
            }
        });
        statsTotalTimeEnduranceIntentionalEndNum = (TextView) findViewById(R.id.statsTotalTimeEnduranceIntentionalEndNum);
        statsTotalTimeEnduranceIntentionalEndPersonalBestLabel = (TextView) findViewById(R.id.statsTotalTimeEnduranceIntentionalEndPersonalBestLabel);
        statsTotalTimeEnduranceIntentionalEndPersonalBestLabel.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showInfoDialog("Total time spent in...", "...endurance runs that ended intentionally where a personal best was broken:\n\n" +
                        "The combined amount of time you spent on all endurance runs where you juggled for longer than" +
                        " all previous endurance runs with the exact same pattern specifics & modifiers" +
                        " where you also ended intentionally.");
                return true;
            }
        });
        statsTotalTimeEnduranceIntentionalEndPersonalBestNum = (TextView) findViewById(R.id.statsTotalTimeEnduranceIntentionalEndPersonalBestNum);
        statsTotalTimeEnduranceUnintentionalEndLabel = (TextView) findViewById(R.id.statsTotalTimeEnduranceUnintentionalEndLabel);
        statsTotalTimeEnduranceUnintentionalEndLabel.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showInfoDialog("Total time spent in...", "...endurance runs that ended unintentionally:\n\n" +
                        "The combined amount of time you spent on all endurance runs which ended unintentionally.");
                return true;
            }
        });
        statsTotalTimeEnduranceUnintentionalEndNum = (TextView) findViewById(R.id.statsTotalTimeEnduranceUnintentionalEndNum);
        statsTotalTimeEnduranceUnintentionalEndPersonalBestLabel = (TextView) findViewById(R.id.statsTotalTimeEnduranceUnintentionalEndPersonalBestLabel);
        statsTotalTimeEnduranceUnintentionalEndPersonalBestLabel.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showInfoDialog("Total time spent in...", "...endurance runs that ended unintentionally where a personal best was broken:\n\n" +
                        "The combined amount of time you spent on all endurance runs where you juggled for longer than" +
                        " all previous endurance runs with the exact same pattern specifics & modifiers" +
                        " where you also ended unintentionally.");
                return true;
            }
        });
        statsTotalTimeEnduranceUnintentionalEndPersonalBestNum = (TextView) findViewById(R.id.statsTotalTimeEnduranceUnintentionalEndPersonalBestNum);
        statsTotalTimeInitialEnduranceLabel = (TextView) findViewById(R.id.statsTotalTimeInitialEnduranceLabel);
        statsTotalTimeInitialEnduranceLabel.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showInfoDialog("Total time spent in...", "...initial endurance runs:\n\n" +
                        "The combined amount of time you spent on runs in your history which have no other endurance runs which exist " +
                        "prior to them with the exact same pattern specifics and modifiers.");
                return true;
            }
        });
        statsTotalTimeInitialEnduranceNum = (TextView) findViewById(R.id.statsTotalTimeInitialEnduranceNum);
        statsTotalTimeDrillsLabel = (TextView) findViewById(R.id.statsTotalTimeDrillsLabel);
        statsTotalTimeDrillsLabel.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showInfoDialog("Total time spent in...", "...drill runs:\n\n" +
                        "The combined amount of time you spent on all drill runs.");
                return true;
            }
        });
        statsTotalTimeDrillsNum = (TextView) findViewById(R.id.statsTotalTimeDrillsNum);
        statsTotalTimeDrillsRestartLabel = (TextView) findViewById(R.id.statsTotalTimeDrillsRestartLabel);
        statsTotalTimeDrillsRestartLabel.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showInfoDialog("Total time spent in...", "...drill runs in \"restart set on unintentionalEnds\" mode:\n\n" +
                        "The combined amount of time you spent on all drill runs in which the \"restart set on unintentionalEnds\" checkbox" +
                        "was checked.");
                return true;
            }
        });
        statsTotalTimeDrillsRestartNum = (TextView) findViewById(R.id.statsTotalTimeDrillsRestartNum);
        statsTotalTimeDrillsRestartDroplessLabel = (TextView) findViewById(R.id.statsTotalTimeDrillsRestartDroplessLabel);
        statsTotalTimeDrillsRestartDroplessLabel.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showInfoDialog("Total time spent in...", "...drill runs in \"restart set on unintentionalEnds\" mode that were dropless:\n\n" +
                        "The combined amount of time you spent on all drill runs in which the \"restart set on unintentionalEnds\" checkbox" +
                        "was checked and you did not unintentionalEnd at all.");
                return true;
            }
        });
        statsTotalTimeDrillsRestartDroplessNum = (TextView) findViewById(R.id.statsTotalTimeDrillsRestartDroplessNum);
        statsTotalTimeDrillsDontRestartLabel = (TextView) findViewById(R.id.statsTotalTimeDrillsDontRestartLabel);
        statsTotalTimeDrillsDontRestartLabel.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showInfoDialog("Total time spent in...", "...drill runs in \"don't restart set on unintentionalEnds\" mode:\n\n" +
                        "The combined amount of time you spent on all drill runs in which the \"restart set on unintentionalEnds\" checkbox" +
                        "was NOT checked.");
                return true;
            }
        });
        statsTotalTimeDrillsDontRestartNum = (TextView) findViewById(R.id.statsTotalTimeDrillsDontRestartNum);
        statsTotalTimeInitialDrillLabel = (TextView) findViewById(R.id.statsTotalTimeInitialDrillLabel);
        statsTotalTimeInitialDrillLabel.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showInfoDialog("Total time spent in...", "...initial drill runs:\n\n" +
                        "The combined amount of time you spent on runs in your history which have no other drill runs which exist " +
                        "prior to them with the exact same pattern specifics and modifiers.");
                return true;
            }
        });
        statsTotalTimeInitialDrillNum = (TextView) findViewById(R.id.statsTotalTimeInitialDrillNum);
        statsTotalTimeEndSpacer = (TextView) findViewById(R.id.statsTotalTimeEndSpacer);

        statsDaysSinceLabel = (TextView) findViewById(R.id.statsDaysSinceLabel);
        statsDaysSinceLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                List<TextView> textViewList = new ArrayList<TextView>();
                textViewList.add(statsDaysSinceRunLabel);
                textViewList.add(statsDaysSinceRunNum);
                textViewList.add(statsDaysSinceEnduranceLabel);
                textViewList.add(statsDaysSinceEnduranceNum);
                textViewList.add(statsDaysSinceEnduranceBrokePersonalBestLabel);
                textViewList.add(statsDaysSinceEnduranceBrokePersonalBestNum);
                textViewList.add(statsDaysSinceEnduranceIntentionalEndLabel);
                textViewList.add(statsDaysSinceEnduranceIntentionalEndNum);
                textViewList.add(statsDaysSinceEnduranceUnintentionalEndLabel);
                textViewList.add(statsDaysSinceEnduranceUnintentionalEndNum);
                textViewList.add(statsDaysSinceDrillLabel);
                textViewList.add(statsDaysSinceDrillNum);
                textViewList.add(statsDaysSinceDrillRestartLabel);
                textViewList.add(statsDaysSinceDrillRestartNum);
                textViewList.add(statsDaysSinceDrillRestartDroplessLabel);
                textViewList.add(statsDaysSinceDrillRestartDroplessNum);
                textViewList.add(statsDaysSinceDrillDontRestartLabel);
                textViewList.add(statsDaysSinceDrillDontRestartNum);
                textViewList.add(statsDaysSinceInitialEnduranceLabel);
                textViewList.add(statsDaysSinceInitialEnduranceNum);
                textViewList.add(statsDaysSinceInitialDrillLabel);
                textViewList.add(statsDaysSinceInitialDrillNum);
                textViewList.add(statsDaysSincePersonalBestIntentionalEndLabel);
                textViewList.add(statsDaysSincePersonalBestIntentionalEndNum);
                textViewList.add(statsDaysSincePersonalBestUnintentionalEndLabel);
                textViewList.add(statsDaysSincePersonalBestUnintentionalEndNum);
                textViewList.add(statsDaysSinceEndSpacer);

                statsToggleTextViewVisibility(statsDaysSinceLabel, textViewList);

            }
        });
        statsDaysSinceLabel.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showInfoDialog("Days since...", "This section shows the number of days since each of the things indicated" +
                        " have occured. If the the most recent run was less than 24 hours ago then then 0 will be shown.");
                return true;
            }
        });
        statsDaysSinceRunLabel = (TextView) findViewById(R.id.statsDaysSinceRunLabel);
        statsDaysSinceRunLabel.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showInfoDialog("Days since...", "...a run:\n\n" +
                        "The number of days that have passed since you have completed a run of any type.");
                return true;
            }
        });
        statsDaysSinceRunNum = (TextView) findViewById(R.id.statsDaysSinceRunNum);
        statsDaysSinceEnduranceLabel = (TextView) findViewById(R.id.statsDaysSinceEnduranceLabel);
        statsDaysSinceEnduranceLabel.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showInfoDialog("Days since...", "...an endurance run:\n\n" +
                        "The number of days that have passed since you have completed an endurance run.");
                return true;
            }
        });
        statsDaysSinceEnduranceNum = (TextView) findViewById(R.id.statsDaysSinceEnduranceNum);
        statsDaysSinceEnduranceBrokePersonalBestLabel = (TextView) findViewById(R.id.statsDaysSinceEnduranceBrokePersonalBestLabel);
        statsDaysSinceEnduranceBrokePersonalBestLabel.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showInfoDialog("Days since...", "...an endurance run where a personal best was broken:\n\n" +
                        "The number of days that have passed since you have completed an endurance run in which you broke a" +
                        " personal best.");
                return true;
            }
        });
        statsDaysSinceEnduranceBrokePersonalBestNum = (TextView) findViewById(R.id.statsDaysSinceEnduranceBrokePersonalBestNum);
        statsDaysSinceEnduranceIntentionalEndLabel = (TextView) findViewById(R.id.statsDaysSinceEnduranceIntentionalEndLabel);
        statsDaysSinceEnduranceIntentionalEndLabel.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showInfoDialog("Days since...", "...an endurance run that ended intentionally:\n\n" +
                        "The number of days that have passed since you have completed an endurance run which ended intentionally.");
                return true;
            }
        });
        statsDaysSinceEnduranceIntentionalEndNum = (TextView) findViewById(R.id.statsDaysSinceEnduranceIntentionalEndNum);
        statsDaysSinceEnduranceUnintentionalEndLabel = (TextView) findViewById(R.id.statsDaysSinceEnduranceUnintentionalEndLabel);
        statsDaysSinceEnduranceUnintentionalEndLabel.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showInfoDialog("Days since...", "...an endurance run that ended unintentionally:\n\n" +
                        "The number of days that have passed since you have completed an endurance run which ended unintentionally.");
                return true;
            }
        });
        statsDaysSinceEnduranceUnintentionalEndNum = (TextView) findViewById(R.id.statsDaysSinceEnduranceUnintentionalEndNum);
        statsDaysSinceDrillLabel = (TextView) findViewById(R.id.statsDaysSinceDrillLabel);
        statsDaysSinceDrillLabel.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showInfoDialog("Days since...", "...an drill run:\n\n" +
                        "The number of days that have passed since you have completed a drill run.");
                return true;
            }
        });
        statsDaysSinceDrillNum = (TextView) findViewById(R.id.statsDaysSinceDrillNum);
        statsDaysSinceDrillRestartLabel = (TextView) findViewById(R.id.statsDaysSinceDrillRestartLabel);
        statsDaysSinceDrillRestartLabel.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showInfoDialog("Days since...", "...a drill run in \"restart set on unintentionalEnds\" mode:\n\n" +
                        "The number of days that have passed since you have completed a drill run in which " +
                        "the \"restart set on unintentionalEnds\" checkbox was checked.");
                return true;
            }
        });
        statsDaysSinceDrillRestartNum = (TextView) findViewById(R.id.statsDaysSinceDrillRestartNum);
        statsDaysSinceDrillRestartDroplessLabel = (TextView) findViewById(R.id.statsDaysSinceDrillRestartDroplessLabel);
        statsDaysSinceDrillRestartDroplessLabel.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showInfoDialog("Days since...", "...a drill run in \"restart set on unintentionalEnds\" mode that was dropless:\n\n" +
                        "The number of days that have passed since you have completed a drill run in which " +
                        "the \"restart set on unintentionalEnds\" checkbox was checked and you did not unintentionalEnd at all.");
                return true;
            }
        });
        statsDaysSinceDrillRestartDroplessNum = (TextView) findViewById(R.id.statsDaysSinceDrillRestartDroplessNum);
        statsDaysSinceDrillDontRestartLabel = (TextView) findViewById(R.id.statsDaysSinceDrillDontRestartLabel);
        statsDaysSinceDrillDontRestartLabel.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showInfoDialog("Days since...", "...a drill run in \"don't restart set on unintentionalEnds\" mode:\n\n" +
                        "The number of days that have passed since you have completed a drill run in which " +
                        "the \"restart set on unintentionalEnds\" checkbox was NOT checked.");
                return true;
            }
        });
        statsDaysSinceDrillDontRestartNum = (TextView) findViewById(R.id.statsDaysSinceDrillDontRestartNum);
        statsDaysSinceInitialEnduranceLabel = (TextView) findViewById(R.id.statsDaysSinceInitialEnduranceLabel);
        statsDaysSinceInitialEnduranceLabel.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showInfoDialog("Days since...", "...an initial endurance run:\n\n" +
                        "The number of days that have passed since you have completed an endurance run which has pattern " +
                        "specifics and modifiers which have no prior history.");

                return true;
            }
        });
        statsDaysSinceInitialEnduranceNum = (TextView) findViewById(R.id.statsDaysSinceInitialEnduranceNum);
        statsDaysSinceInitialDrillLabel = (TextView) findViewById(R.id.statsDaysSinceInitialDrillLabel);
        statsDaysSinceInitialDrillLabel.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showInfoDialog("Days since...", "...an initial drill run:\n\n" +
                        "The number of days that have passed since you have completed an drill run which has pattern " +
                        "specifics and modifiers which have no prior history.");
                return true;
            }
        });
        statsDaysSinceInitialDrillNum = (TextView) findViewById(R.id.statsDaysSinceInitialDrillNum);
        statsDaysSincePersonalBestIntentionalEndLabel = (TextView) findViewById(R.id.statsDaysSincePersonalBestIntentionalEndLabel);
        statsDaysSincePersonalBestIntentionalEndLabel.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showInfoDialog("Days since...", "...an endurance run that ended intentionally where a personal best was broken:\n\n" +
                        "The number of days that have passed since you have completed an endurance run that ended intentionally in which " +
                        "you broke a personal best.");
                return true;
            }
        });
        statsDaysSincePersonalBestIntentionalEndNum = (TextView) findViewById(R.id.statsDaysSincePersonalBestIntentionalEndNum);
        statsDaysSincePersonalBestUnintentionalEndLabel = (TextView) findViewById(R.id.statsDaysSincePersonalBestUnintentionalEndLabel);
        statsDaysSincePersonalBestUnintentionalEndLabel.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showInfoDialog("Days since...", "...an endurance run that ended unintentionally where a personal best was broken:\n\n" +
                        "The number of days that have passed since you have completed an endurance run that ended unintentionally in which " +
                        "you broke a personal best.");
                return true;
            }
        });
        statsDaysSincePersonalBestUnintentionalEndNum = (TextView) findViewById(R.id.statsDaysSincePersonalBestUnintentionalEndNum);
        statsDaysSinceEndSpacer = (TextView) findViewById(R.id.statsDaysSinceEndSpacer);

        statsAverageNumberOfBlankPerXEditTextNumberDaysLabel = (TextView) findViewById(R.id.statsAverageNumberOfBlankPerXEditTextNumberDaysLabel);
        statsAverageNumberOfBlankPerXEditTextNumberDaysLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                List<EditText> editTextList = new ArrayList<EditText>();
                editTextList.add(statsAverageNumberOfBlankPerXEditText);
                statsToggleEditTextVisibility(editTextList);

                List<Button> ButtonList = new ArrayList<Button>();
                ButtonList.add(statsAverageNumberOfBlankPerXEditTextButton);
                statsToggleButtonVisibility(ButtonList);

                List<TextView> textViewList = new ArrayList<TextView>();
                textViewList.add(statsAverageNumberOfBlankPerXEditTextNumberDaysDefineXLabel);
                textViewList.add(statsAverageNumberOfBlankPerXEditTextNumberDaysRunLabel);
                textViewList.add(statsAverageNumberOfBlankPerXEditTextNumberDaysRunNum);
                textViewList.add(statsAverageNumberOfBlankPerXEditTextNumberDaysEnduranceRunLabel);
                textViewList.add(statsAverageNumberOfBlankPerXEditTextNumberDaysEnduranceRunNum);
                textViewList.add(statsAverageNumberOfBlankPerXEditTextNumberDaysEnduranceRunPersonalBestLabel);
                textViewList.add(statsAverageNumberOfBlankPerXEditTextNumberDaysEnduranceRunPersonalBestNum);
                textViewList.add(statsAverageNumberOfBlankPerXEditTextNumberDaysEnduranceRunIntentionalEndLabel);
                textViewList.add(statsAverageNumberOfBlankPerXEditTextNumberDaysEnduranceRunIntentionalEndNum);
                textViewList.add(statsAverageNumberOfBlankPerXEditTextNumberDaysEnduranceRunIntentionalEndPersonalBestLabel);
                textViewList.add(statsAverageNumberOfBlankPerXEditTextNumberDaysEnduranceRunIntentionalEndPersonalBestNum);
                textViewList.add(statsAverageNumberOfBlankPerXEditTextNumberDaysEnduranceRunUnintentionalEndLabel);
                textViewList.add(statsAverageNumberOfBlankPerXEditTextNumberDaysEnduranceRunUnintentionalEndNum);
                textViewList.add(statsAverageNumberOfBlankPerXEditTextNumberDaysEnduranceRunUnintentionalEndPersonalBestLabel);
                textViewList.add(statsAverageNumberOfBlankPerXEditTextNumberDaysEnduranceRunUnintentionalEndPersonalBestNum);
                textViewList.add(statsAverageNumberOfBlankPerXEditTextNumberDaysInitialEnduranceLabel);
                textViewList.add(statsAverageNumberOfBlankPerXEditTextNumberDaysInitialEnduranceNum);
                textViewList.add(statsAverageNumberOfBlankPerXEditTextNumberDaysDrillRunLabel);
                textViewList.add(statsAverageNumberOfBlankPerXEditTextNumberDaysDrillRunNum);
                textViewList.add(statsAverageNumberOfBlankPerXEditTextNumberDaysDrillRunRestartLabel);
                textViewList.add(statsAverageNumberOfBlankPerXEditTextNumberDaysDrillRunRestartNum);
                textViewList.add(statsAverageNumberOfBlankPerXEditTextNumberDaysDrillRunRestartDroplessLabel);
                textViewList.add(statsAverageNumberOfBlankPerXEditTextNumberDaysDrillRunRestartDroplessNum);
                textViewList.add(statsAverageNumberOfBlankPerXEditTextNumberDaysDrillRunDontRestartLabel);
                textViewList.add(statsAverageNumberOfBlankPerXEditTextNumberDaysDrillRunDontRestartNum);
                textViewList.add(statsAverageNumberOfBlankPerXEditTextNumberDaysInitialDrillRunLabel);
                textViewList.add(statsAverageNumberOfBlankPerXEditTextNumberDaysInitialDrillRunNum);
                textViewList.add(statsAverageNumberOfBlankPerXEditTextNumberDaysEndSpacer);
                statsToggleTextViewVisibility(statsAverageNumberOfBlankPerXEditTextNumberDaysLabel, textViewList);

            }
        });
        statsAverageNumberOfBlankPerXEditTextNumberDaysLabel.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showInfoDialog("Average number of ... per X day/s", "This shows the averages indicated based on the number of X days." +
                        "  Use the blank below to define X.  For example, if X is defined as '1', then the average shown next to " +
                        "'...endurance runs...' would be the average number of endurance runs that you complete each day.");
                return true;
            }
        });
        statsAverageNumberOfBlankPerXEditText = (EditText) findViewById(R.id.statsAverageNumberOfBlankPerXEditText);
        statsAverageNumberOfBlankPerXEditTextButton = (Button) findViewById(R.id.statsAverageNumberOfBlankPerXEditTextButton);
        statsAverageNumberOfBlankPerXEditTextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refreshStats();
            }
        });
        statsAverageNumberOfBlankPerXEditTextButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showInfoDialog("OK", "This will update the stats numbers based on what X has been defined as in the blank to the left. " +
                        " X is the number of days used in figuring out the averages indicated. For example, if " +
                        "X is defined as '7', then the average shown next to '...runs...' would be the average number of runs that" +
                        " you complete per week.");
                return false;
            }
        });
        statsAverageNumberOfBlankPerXEditTextNumberDaysDefineXLabel = (TextView) findViewById(R.id.statsAverageNumberOfBlankPerXEditTextNumberDaysDefineXLabel);
        statsAverageNumberOfBlankPerXEditTextNumberDaysDefineXLabel.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showInfoDialog("Define X", "Use the blank above to define X." +
                        " X is the number of days used in figuring out the averages indicated. For example, if " +
                        "X is defined as '30', then the average shown next to '...drill runs...' would be the average number of " +
                        "drill runs that you complete in a month.");
                return true;
            }
        });
        statsAverageNumberOfBlankPerXEditTextNumberDaysRunLabel = (TextView) findViewById(R.id.statsAverageNumberOfBlankPerXEditTextNumberDaysRunLabel);
        statsAverageNumberOfBlankPerXEditTextNumberDaysRunLabel.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showInfoDialog("Average number of...", "...runs per X day/s:\n\n" +
                        "The formula used to determine this average is: 'total number of runs' / " +
                        "('days since earliest run' / X)");
                return true;
            }
        });
        statsAverageNumberOfBlankPerXEditTextNumberDaysRunNum = (TextView) findViewById(R.id.statsAverageNumberOfBlankPerXEditTextNumberDaysRunNum);
        statsAverageNumberOfBlankPerXEditTextNumberDaysEnduranceRunLabel = (TextView) findViewById(R.id.statsAverageNumberOfBlankPerXEditTextNumberDaysEnduranceRunLabel);
        statsAverageNumberOfBlankPerXEditTextNumberDaysEnduranceRunLabel.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showInfoDialog("Average number of...", "...endurance runs per X day/s:\n\n" +
                        "The formula used to determine this average is: 'total number of endurance runs' / " +
                        "('days since earliest endurance run' / X)");
                return true;
            }
        });
        statsAverageNumberOfBlankPerXEditTextNumberDaysEnduranceRunNum = (TextView) findViewById(R.id.statsAverageNumberOfBlankPerXEditTextNumberDaysEnduranceRunNum);
        statsAverageNumberOfBlankPerXEditTextNumberDaysEnduranceRunPersonalBestLabel = (TextView) findViewById(R.id.statsAverageNumberOfBlankPerXEditTextNumberDaysEnduranceRunPersonalBestLabel);
        statsAverageNumberOfBlankPerXEditTextNumberDaysEnduranceRunPersonalBestLabel.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showInfoDialog("Average number of...", "...endurance runs where a personal best was broken per X day/s\n\n" +
                        "The formula used to determine this average is: 'total number of endurance runs where a personal best was broken'" +
                        " / ('days since earliest endurance run' / X)");
                return true;
            }
        });
        statsAverageNumberOfBlankPerXEditTextNumberDaysEnduranceRunPersonalBestNum = (TextView) findViewById(R.id.statsAverageNumberOfBlankPerXEditTextNumberDaysEnduranceRunPersonalBestNum);
        statsAverageNumberOfBlankPerXEditTextNumberDaysEnduranceRunIntentionalEndLabel = (TextView) findViewById(R.id.statsAverageNumberOfBlankPerXEditTextNumberDaysEnduranceRunIntentionalEndLabel);
        statsAverageNumberOfBlankPerXEditTextNumberDaysEnduranceRunIntentionalEndLabel.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showInfoDialog("Average number of...", "...endurance runs that ended intentionally per X day/s:\n\n" +
                        "The formula used to determine this average is: 'total number of endurance runs that ended intentionally'" +
                        " / ('days since earliest endurance run' / X)");
                return true;
            }
        });
        statsAverageNumberOfBlankPerXEditTextNumberDaysEnduranceRunIntentionalEndNum = (TextView) findViewById(R.id.statsAverageNumberOfBlankPerXEditTextNumberDaysEnduranceRunIntentionalEndNum);
        statsAverageNumberOfBlankPerXEditTextNumberDaysEnduranceRunIntentionalEndPersonalBestLabel = (TextView) findViewById(R.id.statsAverageNumberOfBlankPerXEditTextNumberDaysEnduranceRunIntentionalEndPersonalBestLabel);
        statsAverageNumberOfBlankPerXEditTextNumberDaysEnduranceRunIntentionalEndPersonalBestLabel.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showInfoDialog("Average number of...", "...endurance runs that ended intentionally where a personal best was broken per X day/s:\n\n" +
                        "The formula used to determine this average is: 'total number of endurance runs that ended intentionally" +
                        " where a personal best was broken' / ('days since earliest endurance run' / X)");
                return true;
            }
        });
        statsAverageNumberOfBlankPerXEditTextNumberDaysEnduranceRunIntentionalEndPersonalBestNum = (TextView) findViewById(R.id.statsAverageNumberOfBlankPerXEditTextNumberDaysEnduranceRunIntentionalEndPersonalBestNum);
        statsAverageNumberOfBlankPerXEditTextNumberDaysEnduranceRunUnintentionalEndLabel = (TextView) findViewById(R.id.statsAverageNumberOfBlankPerXEditTextNumberDaysEnduranceRunUnintentionalEndLabel);
        statsAverageNumberOfBlankPerXEditTextNumberDaysEnduranceRunUnintentionalEndLabel.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showInfoDialog("Average number of...", "...endurance runs that ended unintentionally per X day/s:\n\n" +
                        "The formula used to determine this average is: 'total number of endurance runs that ended unintentionally'" +
                        " / ('days since earliest endurance run' / X)");
                return true;
            }
        });
        statsAverageNumberOfBlankPerXEditTextNumberDaysEnduranceRunUnintentionalEndNum = (TextView) findViewById(R.id.statsAverageNumberOfBlankPerXEditTextNumberDaysEnduranceRunUnintentionalEndNum);
        statsAverageNumberOfBlankPerXEditTextNumberDaysEnduranceRunUnintentionalEndPersonalBestLabel = (TextView) findViewById(R.id.statsAverageNumberOfBlankPerXEditTextNumberDaysEnduranceRunUnintentionalEndPersonalBestLabel);
        statsAverageNumberOfBlankPerXEditTextNumberDaysEnduranceRunUnintentionalEndPersonalBestLabel.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showInfoDialog("Average number of...", "...endurance runs that ended unintentionally where a personal best was broken per X day/s:\n\n" +
                        "The formula used to determine this average is: 'total number of endurance runs that ended unintentionally" +
                        " where a personal best was broken' / ('days since earliest endurance run' / X)");
                return true;
            }
        });
        statsAverageNumberOfBlankPerXEditTextNumberDaysEnduranceRunUnintentionalEndPersonalBestNum = (TextView) findViewById(R.id.statsAverageNumberOfBlankPerXEditTextNumberDaysEnduranceRunUnintentionalEndPersonalBestNum);
        statsAverageNumberOfBlankPerXEditTextNumberDaysInitialEnduranceLabel = (TextView) findViewById(R.id.statsAverageNumberOfBlankPerXEditTextNumberDaysInitialEnduranceLabel);
        statsAverageNumberOfBlankPerXEditTextNumberDaysEnduranceRunLabel.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showInfoDialog("Average number of...", "...initial endurance runs per X day/s:\n\n" +
                        "The formula used to determine this average is: 'total number of initial* endurance runs'" +
                        " / ('days since earliest endurance run' / X)\n\n*'initial' indicates that it is a run with a combination of pattern " +
                        " specifics and modifiers which has no history. It is the first run of it's exact type.");
                return true;
            }
        });
        statsAverageNumberOfBlankPerXEditTextNumberDaysInitialEnduranceNum = (TextView) findViewById(R.id.statsAverageNumberOfBlankPerXEditTextNumberDaysInitialEnduranceNum);
        statsAverageNumberOfBlankPerXEditTextNumberDaysDrillRunLabel = (TextView) findViewById(R.id.statsAverageNumberOfBlankPerXEditTextNumberDaysDrillRunLabel);
        statsAverageNumberOfBlankPerXEditTextNumberDaysDrillRunLabel.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showInfoDialog("Average number of...", "...drill runs per X day/s:\n\n" +
                        "The formula used to determine this average is: 'total number of drill runs' / " +
                        "('days since earliest drill run' / X)");
                return true;
            }
        });
        statsAverageNumberOfBlankPerXEditTextNumberDaysDrillRunNum = (TextView) findViewById(R.id.statsAverageNumberOfBlankPerXEditTextNumberDaysDrillRunNum);
        statsAverageNumberOfBlankPerXEditTextNumberDaysDrillRunRestartLabel = (TextView) findViewById(R.id.statsAverageNumberOfBlankPerXEditTextNumberDaysDrillRunRestartLabel);
        statsAverageNumberOfBlankPerXEditTextNumberDaysDrillRunRestartLabel.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showInfoDialog("Average number of...", "...drill runs in \"restart set on unintentionalEnds\" mode per X day/s:\n\n" +
                        "The formula used to determine this average is: 'total number of drill runs in which the \"restart set on unintentionalEnds\"" +
                        " checkbox was checked' / ('days since earliest drill run' / X)");
                return true;
            }
        });
        statsAverageNumberOfBlankPerXEditTextNumberDaysDrillRunRestartNum = (TextView) findViewById(R.id.statsAverageNumberOfBlankPerXEditTextNumberDaysDrillRunRestartNum);
        statsAverageNumberOfBlankPerXEditTextNumberDaysDrillRunRestartDroplessLabel = (TextView) findViewById(R.id.statsAverageNumberOfBlankPerXEditTextNumberDaysDrillRunRestartDroplessLabel);
        statsAverageNumberOfBlankPerXEditTextNumberDaysDrillRunRestartDroplessLabel.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showInfoDialog("Average number of...", "...drill runs that are in \"restart set on unintentionalEnds\" mode and dropless per X day/s:\n\n" +
                        "The formula used to determine this average is: 'total number of drill runs in which the \"restart set on unintentionalEnds\"" +
                        " checkbox was checked and you did not unintentionalEnd' / ('days since earliest drill run' / X)");
                return true;
            }
        });
        statsAverageNumberOfBlankPerXEditTextNumberDaysDrillRunRestartDroplessNum = (TextView) findViewById(R.id.statsAverageNumberOfBlankPerXEditTextNumberDaysDrillRunRestartDroplessNum);
        statsAverageNumberOfBlankPerXEditTextNumberDaysDrillRunDontRestartLabel = (TextView) findViewById(R.id.statsAverageNumberOfBlankPerXEditTextNumberDaysDrillRunDontRestartLabel);
        statsAverageNumberOfBlankPerXEditTextNumberDaysDrillRunDontRestartLabel.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showInfoDialog("Average number of...", "...drill runs in \"don't restart set on unintentionalEnds\" mode per X day/s:\n\n" +
                        "The formula used to determine this average is: 'total number of drill runs in which the \"restart set on unintentionalEnds\"" +
                        " checkbox was NOT checked' / ('days since earliest drill run' / X)");
                return true;
            }
        });
        statsAverageNumberOfBlankPerXEditTextNumberDaysDrillRunDontRestartNum = (TextView) findViewById(R.id.statsAverageNumberOfBlankPerXEditTextNumberDaysDrillRunDontRestartNum);
        statsAverageNumberOfBlankPerXEditTextNumberDaysInitialDrillRunLabel = (TextView) findViewById(R.id.statsAverageNumberOfBlankPerXEditTextNumberDaysInitialDrillRunLabel);
        statsAverageNumberOfBlankPerXEditTextNumberDaysInitialDrillRunLabel.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showInfoDialog("Average number of...", "...initial drill runs per X day/s:\n\n" +
                        "The formula used to determine this average is: 'total number of initial* drill runs'" +
                        " / ('days since earliest drill run' / X)\n\n*'initial' indicates that it is a run with a combination of pattern " +
                        " specifics and modifiers which has no history. It is the first run of it's exact type.");
                return true;
            }
        });
        statsAverageNumberOfBlankPerXEditTextNumberDaysInitialDrillRunNum = (TextView) findViewById(R.id.statsAverageNumberOfBlankPerXEditTextNumberDaysInitialDrillRunNum);
        statsAverageNumberOfBlankPerXEditTextNumberDaysEndSpacer = (TextView) findViewById(R.id.statsAverageNumberOfBlankPerXEditTextNumberDaysEndSpacer);

        statsNumberOfBlankInLastXEditTextNumberDaysLabel = (TextView) findViewById(R.id.statsNumberOfBlankInLastXEditTextNumberDaysLabel);
        statsNumberOfBlankInLastXEditTextNumberDaysLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                List<EditText> editTextList = new ArrayList<EditText>();
                editTextList.add(statsNumberOfBlankInLastXEditText);
                statsToggleEditTextVisibility(editTextList);

                List<Button> ButtonList = new ArrayList<Button>();
                ButtonList.add(statsNumberOfBlankInLastXEditTextButton);
                statsToggleButtonVisibility(ButtonList);

                List<TextView> textViewList = new ArrayList<TextView>();
                textViewList.add(statsNumberOfBlankInLastXEditTextNumberDaysDefineXLabel);
                textViewList.add(statsNumberOfBlankInLastXEditTextNumberDaysRunLabel);
                textViewList.add(statsNumberOfBlankInLastXEditTextNumberDaysRunNum);
                textViewList.add(statsNumberOfBlankInLastXEditTextNumberDaysEnduranceRunLabel);
                textViewList.add(statsNumberOfBlankInLastXEditTextNumberDaysEnduranceRunNum);
                textViewList.add(statsNumberOfBlankInLastXEditTextNumberDaysEnduranceRunPersonalBestLabel);
                textViewList.add(statsNumberOfBlankInLastXEditTextNumberDaysEnduranceRunPersonalBestNum);
                textViewList.add(statsNumberOfBlankInLastXEditTextNumberDaysEnduranceRunIntentionalEndLabel);
                textViewList.add(statsNumberOfBlankInLastXEditTextNumberDaysEnduranceRunIntentionalEndNum);
                textViewList.add(statsNumberOfBlankInLastXEditTextNumberDaysEnduranceRunIntentionalEndPersonalBestLabel);
                textViewList.add(statsNumberOfBlankInLastXEditTextNumberDaysEnduranceRunIntentionalEndPersonalBestNum);
                textViewList.add(statsNumberOfBlankInLastXEditTextNumberDaysEnduranceRunUnintentionalEndLabel);
                textViewList.add(statsNumberOfBlankInLastXEditTextNumberDaysEnduranceRunUnintentionalEndNum);
                textViewList.add(statsNumberOfBlankInLastXEditTextNumberDaysEnduranceRunUnintentionalEndPersonalBestLabel);
                textViewList.add(statsNumberOfBlankInLastXEditTextNumberDaysEnduranceRunUnintentionalEndPersonalBestNum);
                textViewList.add(statsNumberOfBlankInLastXEditTextNumberDaysInitialEnduranceLabel);
                textViewList.add(statsNumberOfBlankInLastXEditTextNumberDaysInitialEnduranceNum);
                textViewList.add(statsNumberOfBlankInLastXEditTextNumberDaysDrillRunLabel);
                textViewList.add(statsNumberOfBlankInLastXEditTextNumberDaysDrillRunNum);
                textViewList.add(statsNumberOfBlankInLastXEditTextNumberDaysDrillRunRestartLabel);
                textViewList.add(statsNumberOfBlankInLastXEditTextNumberDaysDrillRunRestartNum);
                textViewList.add(statsNumberOfBlankInLastXEditTextNumberDaysDrillRunRestartDroplessLabel);
                textViewList.add(statsNumberOfBlankInLastXEditTextNumberDaysDrillRunRestartDroplessNum);
                textViewList.add(statsNumberOfBlankInLastXEditTextNumberDaysDrillRunDontRestartLabel);
                textViewList.add(statsNumberOfBlankInLastXEditTextNumberDaysDrillRunDontRestartNum);
                textViewList.add(statsNumberOfBlankInLastXEditTextNumberDaysInitialDrillRunLabel);
                textViewList.add(statsNumberOfBlankInLastXEditTextNumberDaysInitialDrillRunNum);
                textViewList.add(statsNumberOfBlankInLastXEditTextNumberDaysEndSpacer);

                statsToggleTextViewVisibility(statsNumberOfBlankInLastXEditTextNumberDaysLabel, textViewList);

            }
        });
        statsNumberOfBlankInLastXEditTextNumberDaysLabel.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showInfoDialog("Number of blank in last...", "This will show the number of times you did each of the indicated" +
                        " things in the number of days defined as X. For example, if you define X as '7' then the number next to" +
                        " 'runs' will be the number of runs that you have done during the last week.");
                return true;

            }
        });
        statsNumberOfBlankInLastXEditTextNumberDaysDefineXLabel = (TextView) findViewById(R.id.statsNumberOfBlankInLastXEditTextNumberDaysDefineXLabel);
        statsNumberOfBlankInLastXEditTextNumberDaysDefineXLabel.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showInfoDialog("Define X", "Use the blank above to define what X is. X will be used to determine how many days prior to" +
                        " today to be used in figuring out how many of each of things below occurred.");
                return true;
            }
        });
        statsNumberOfBlankInLastXEditText = (EditText) findViewById(R.id.statsNumberOfBlankInLastXEditText);
        statsNumberOfBlankInLastXEditTextButton = (Button) findViewById(R.id.statsNumberOfBlankInLastXEditTextButton);
        statsNumberOfBlankInLastXEditTextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refreshStats();
            }
        });
        statsNumberOfBlankInLastXEditTextButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showInfoDialog("OK", "This will update the stats numbers based on what X has been defined as in the blank to the left." +
                        " X will be used to determine how many days prior to today to be used in figuring out how many of each of things" +
                        " below occurred.");
                return true;
            }
        });
        statsNumberOfBlankInLastXEditTextNumberDaysRunLabel = (TextView) findViewById(R.id.statsNumberOfBlankInLastXEditTextNumberDaysRunLabel);
        statsNumberOfBlankInLastXEditTextNumberDaysRunLabel.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showInfoDialog("Number of...", "...runs in the last X days:\n\nThis is the total number of runs that you have completed" +
                        " in the last X days. You can define what X is in the blank above.");
                return true;
            }
        });
        statsNumberOfBlankInLastXEditTextNumberDaysRunNum = (TextView) findViewById(R.id.statsNumberOfBlankInLastXEditTextNumberDaysRunNum);
        statsNumberOfBlankInLastXEditTextNumberDaysEnduranceRunLabel = (TextView) findViewById(R.id.statsNumberOfBlankInLastXEditTextNumberDaysEnduranceRunLabel);
        statsNumberOfBlankInLastXEditTextNumberDaysEnduranceRunLabel.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showInfoDialog("Number of...", "...endurance runs in the last X days:\n\nThis is the total number of endurance runs that " +
                        "you have completed in the last X days. You can define what X is in the blank above.");
                return true;
            }
        });
        statsNumberOfBlankInLastXEditTextNumberDaysEnduranceRunNum = (TextView) findViewById(R.id.statsNumberOfBlankInLastXEditTextNumberDaysEnduranceRunNum);
        statsNumberOfBlankInLastXEditTextNumberDaysEnduranceRunPersonalBestLabel = (TextView) findViewById(R.id.statsNumberOfBlankInLastXEditTextNumberDaysEnduranceRunPersonalBestLabel);
        statsNumberOfBlankInLastXEditTextNumberDaysEnduranceRunPersonalBestLabel.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showInfoDialog("Number of...", "...endurance runs where a personal best was broken in the last X days:\n\nThis is the " +
                        "total number of endurance runs that you have completed in the last X days in which you broke a personal best. " +
                        "You can define what X is in the blank above.");
                return true;
            }
        });
        statsNumberOfBlankInLastXEditTextNumberDaysEnduranceRunPersonalBestNum = (TextView) findViewById(R.id.statsNumberOfBlankInLastXEditTextNumberDaysEnduranceRunPersonalBestNum);
        statsNumberOfBlankInLastXEditTextNumberDaysEnduranceRunIntentionalEndLabel = (TextView) findViewById(R.id.statsNumberOfBlankInLastXEditTextNumberDaysEnduranceRunIntentionalEndLabel);
        statsNumberOfBlankInLastXEditTextNumberDaysEnduranceRunIntentionalEndLabel.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showInfoDialog("Number of...", "...endurance runs that ended intentionally in the last X days:\n\nThis is the " +
                        "total number of endurance runs that ended intentionally which you have completed in the last X days." +
                        "  You can define what X is in the blank above.");
                return true;
            }
        });
        statsNumberOfBlankInLastXEditTextNumberDaysEnduranceRunIntentionalEndNum = (TextView) findViewById(R.id.statsNumberOfBlankInLastXEditTextNumberDaysEnduranceRunIntentionalEndNum);
        statsNumberOfBlankInLastXEditTextNumberDaysEnduranceRunIntentionalEndPersonalBestLabel = (TextView) findViewById(R.id.statsNumberOfBlankInLastXEditTextNumberDaysEnduranceRunIntentionalEndPersonalBestLabel);
        statsNumberOfBlankInLastXEditTextNumberDaysEnduranceRunIntentionalEndPersonalBestLabel.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showInfoDialog("Number of...", "...endurance runs that ended intentionally where a personal best was broken in the last X days:\n\nThis is the " +
                        "total number of endurance runs that ended intentionally which you have completed in the last X days in " +
                        "which you broke a personal best.  You can define what X is in the blank above.");
                return true;
            }
        });
        statsNumberOfBlankInLastXEditTextNumberDaysEnduranceRunIntentionalEndPersonalBestNum = (TextView) findViewById(R.id.statsNumberOfBlankInLastXEditTextNumberDaysEnduranceRunIntentionalEndPersonalBestNum);
        statsNumberOfBlankInLastXEditTextNumberDaysEnduranceRunUnintentionalEndLabel = (TextView) findViewById(R.id.statsNumberOfBlankInLastXEditTextNumberDaysEnduranceRunUnintentionalEndLabel);
        statsNumberOfBlankInLastXEditTextNumberDaysEnduranceRunUnintentionalEndLabel.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showInfoDialog("Number of...", "...endurance runs that ended unintentionally in the last X days:\n\nThis is the " +
                        "total number of endurance runs that ended unintentionally which you have completed in the last X days." +
                        "  You can define what X is in the blank above.");
                return true;
            }
        });
        statsNumberOfBlankInLastXEditTextNumberDaysEnduranceRunUnintentionalEndNum = (TextView) findViewById(R.id.statsNumberOfBlankInLastXEditTextNumberDaysEnduranceRunUnintentionalEndNum);
        statsNumberOfBlankInLastXEditTextNumberDaysEnduranceRunUnintentionalEndPersonalBestLabel = (TextView) findViewById(R.id.statsNumberOfBlankInLastXEditTextNumberDaysEnduranceRunUnintentionalEndPersonalBestLabel);
        statsNumberOfBlankInLastXEditTextNumberDaysEnduranceRunUnintentionalEndPersonalBestLabel.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showInfoDialog("Number of...", "...endurance runs that ended unintentionally where a personal best was broken in the last X days:\n\nThis is the " +
                        "total number of endurance runs that ended unintentionally which you have completed in the last X days in " +
                        "which you broke a personal best.  You can define what X is in the blank above.");
                return true;
            }
        });
        statsNumberOfBlankInLastXEditTextNumberDaysEnduranceRunUnintentionalEndPersonalBestNum = (TextView) findViewById(R.id.statsNumberOfBlankInLastXEditTextNumberDaysEnduranceRunUnintentionalEndPersonalBestNum);
        statsNumberOfBlankInLastXEditTextNumberDaysInitialEnduranceLabel = (TextView) findViewById(R.id.statsNumberOfBlankInLastXEditTextNumberDaysInitialEnduranceLabel);
        statsNumberOfBlankInLastXEditTextNumberDaysInitialEnduranceLabel.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showInfoDialog("Number of...", "...initial endurance runs in the last X days:\n\nThis is the " +
                        "total number of endurance runs which have no prior endurance history that you have done in the last X days." +
                        "  You can define what X is in the blank above.");
                return true;
            }
        });
        statsNumberOfBlankInLastXEditTextNumberDaysInitialEnduranceNum = (TextView) findViewById(R.id.statsNumberOfBlankInLastXEditTextNumberDaysInitialEnduranceNum);
        statsNumberOfBlankInLastXEditTextNumberDaysDrillRunLabel = (TextView) findViewById(R.id.statsNumberOfBlankInLastXEditTextNumberDaysDrillRunLabel);
        statsNumberOfBlankInLastXEditTextNumberDaysDrillRunLabel.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showInfoDialog("Number of...", "...drill runs in the last X days:\n\nThis is the total number of drill runs that " +
                        "you have completed in the last X days. You can define what X is in the blank above.");
                return true;
            }
        });
        statsNumberOfBlankInLastXEditTextNumberDaysDrillRunNum = (TextView) findViewById(R.id.statsNumberOfBlankInLastXEditTextNumberDaysDrillRunNum);
        statsNumberOfBlankInLastXEditTextNumberDaysDrillRunRestartLabel = (TextView) findViewById(R.id.statsNumberOfBlankInLastXEditTextNumberDaysDrillRunRestartLabel);
        statsNumberOfBlankInLastXEditTextNumberDaysDrillRunRestartLabel.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showInfoDialog("Number of...", "...drill runs in \"restart set on unintentionalEnds\" mode in the last X days:\n\nThis is the total number of drill runs that " +
                        "you have completed in the last X days with the \"restart set on unintentionalEnds\" checkbox checked. You can define what X " +
                        "is in the blank above.");
                return true;
            }
        });
        statsNumberOfBlankInLastXEditTextNumberDaysDrillRunRestartNum = (TextView) findViewById(R.id.statsNumberOfBlankInLastXEditTextNumberDaysDrillRunRestartNum);
        statsNumberOfBlankInLastXEditTextNumberDaysDrillRunRestartDroplessLabel = (TextView) findViewById(R.id.statsNumberOfBlankInLastXEditTextNumberDaysDrillRunRestartDroplessLabel);
        statsNumberOfBlankInLastXEditTextNumberDaysDrillRunRestartDroplessLabel.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showInfoDialog("Number of...", "...drill runs in \"restart set on unintentionalEnds\"mode that were dropless in the last X days:\n\nThis is the total number " +
                        "of drill runs that you have completed in the last X days with the \"restart set on unintentionalEnds\" checkbox checked without unintentionalEndping at all." +
                        " You can define what X is in the blank above.");
                return true;
            }
        });
        statsNumberOfBlankInLastXEditTextNumberDaysDrillRunRestartDroplessNum = (TextView) findViewById(R.id.statsNumberOfBlankInLastXEditTextNumberDaysDrillRunRestartDroplessNum);
        statsNumberOfBlankInLastXEditTextNumberDaysDrillRunDontRestartLabel = (TextView) findViewById(R.id.statsNumberOfBlankInLastXEditTextNumberDaysDrillRunDontRestartLabel);
        statsNumberOfBlankInLastXEditTextNumberDaysDrillRunDontRestartLabel.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showInfoDialog("Number of...", "...drill runs in \"don't restart set on unintentionalEnds\"mode in the last X days:\n\nThis is the total number of drill runs that " +
                        "you have completed in the last X days with the \"restart set on unintentionalEnds\" checkbox NOT checked. You can define what X " +
                        "is in the blank above.");
                return true;
            }
        });
        statsNumberOfBlankInLastXEditTextNumberDaysDrillRunDontRestartNum = (TextView) findViewById(R.id.statsNumberOfBlankInLastXEditTextNumberDaysDrillRunDontRestartNum);
        statsNumberOfBlankInLastXEditTextNumberDaysInitialDrillRunLabel = (TextView) findViewById(R.id.statsNumberOfBlankInLastXEditTextNumberDaysInitialDrillRunLabel);
        statsNumberOfBlankInLastXEditTextNumberDaysInitialDrillRunLabel.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showInfoDialog("Number of...", "...initial drill runs in the last X days:\n\nThis is the " +
                        "total number of drill runs which have no prior drill history that you have done in the last X days." +
                        "  You can define what X is in the blank above.");
                return true;
            }
        });
        statsNumberOfBlankInLastXEditTextNumberDaysInitialDrillRunNum = (TextView) findViewById(R.id.statsNumberOfBlankInLastXEditTextNumberDaysInitialDrillRunNum);
        statsNumberOfBlankInLastXEditTextNumberDaysEndSpacer = (TextView) findViewById(R.id.statsNumberOfBlankInLastXEditTextNumberDaysEndSpacer);

        statsPercentEndurLabel = (TextView) findViewById(R.id.statsPercentEndurLabel);
        statsPercentEndurLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                List<TextView> textViewList = new ArrayList<TextView>();
                textViewList.add(statsPercentEndurEndIntentionalEndLabel);
                textViewList.add(statsPercentEndurEndIntentionalEndNum);
                textViewList.add(statsPercentEndurEndUnintentionalEndLabel);
                textViewList.add(statsPercentEndurEndUnintentionalEndNum);
                textViewList.add(statsPercentEndurAnyPersonalBestBrokeLabel);
                textViewList.add(statsPercentEndurAnyPersonalBestBrokeNum);
                textViewList.add(statsPercentEndurIntentionalEndPersonalBestBrokeLabel);
                textViewList.add(statsPercentEndurIntentionalEndPersonalBestBrokeNum);
                textViewList.add(statsPercentEndurUnintentionalEndPersonalBestBrokeLabel);
                textViewList.add(statsPercentEndurUnintentionalEndPersonalBestBrokeNum);
                textViewList.add(statsPercentEndurEndSpacer);

                statsToggleTextViewVisibility(statsPercentEndurLabel, textViewList);

            }
        });
        statsPercentEndurLabel.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showInfoDialog("Percent of endurance runs...", "These will show the percent of the runs in which you" +
                        " did the following thing.");
                return true;

            }
        });
        statsPercentEndurEndIntentionalEndLabel = (TextView) findViewById(R.id.statsPercentEndurEndIntentionalEndLabel);
        statsPercentEndurEndIntentionalEndLabel.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showInfoDialog("Percent of endurance runs...", "...that have ended intentionally:\n\n" +
                        "The percent of your endurance runs which have ended intentionally. To find this number," +
                        " the following formula wa used; 100*(runs that ended intentionally / all endurance runs)");
                return true;
            }
        });
        statsPercentEndurEndIntentionalEndNum = (TextView) findViewById(R.id.statsPercentEndurEndIntentionalEndNum);
        statsPercentEndurEndUnintentionalEndLabel = (TextView) findViewById(R.id.statsPercentEndurEndUnintentionalEndLabel);
        statsPercentEndurEndUnintentionalEndLabel.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showInfoDialog("Percent of endurance runs...", "...that have ended unintentionally:\n\n" +
                        "The percent of your endurance runs which have ended unintentionally. To find this number," +
                        " the following formula wa used; 100*(runs that ended unintentionally / all endurance runs)");
                return true;
            }
        });
        statsPercentEndurEndUnintentionalEndNum = (TextView) findViewById(R.id.statsPercentEndurEndUnintentionalEndNum);
        statsPercentEndurAnyPersonalBestBrokeLabel = (TextView) findViewById(R.id.statsPercentEndurAnyPersonalBestBrokeLabel);
        statsPercentEndurAnyPersonalBestBrokeLabel.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showInfoDialog("Percent of endurance runs...", "...(non-initial) in which a personal best was broken:\n\n" +
                        "The percent of your endurance runs(not counting initial runs), in which you broke a previously" +
                        " set personal best record.  To find this number, the following formula was used; 100*(runs in" +
                        " which you broke a personal record / all non-initial endurance runs)");


                return true;
            }
        });
        statsPercentEndurAnyPersonalBestBrokeNum = (TextView) findViewById(R.id.statsPercentEndurAnyPersonalBestBrokeNum);
        statsPercentEndurIntentionalEndPersonalBestBrokeLabel = (TextView) findViewById(R.id.statsPercentEndurIntentionalEndPersonalBestBrokeLabel);
        statsPercentEndurIntentionalEndPersonalBestBrokeLabel.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showInfoDialog("Percent of endurance runs...", "...(non-initial) which ended intentionally and a personal best was broken:\n\n" +
                        "The percent of your intentionally-ended endurance runs(not counting initial runs), in which you broke a previously" +
                        " set personal best record.  To find this number, the following formula was used; 100*(runs in" +
                        " which you intentionally-ended and broke a personal record / all non-initial, intentionally-ended, endurance runs)");
                return true;
            }
        });
        statsPercentEndurIntentionalEndPersonalBestBrokeNum = (TextView) findViewById(R.id.statsPercentEndurIntentionalEndPersonalBestBrokeNum);
        statsPercentEndurUnintentionalEndPersonalBestBrokeLabel = (TextView) findViewById(R.id.statsPercentEndurUnintentionalEndPersonalBestBrokeLabel);
        statsPercentEndurUnintentionalEndPersonalBestBrokeLabel.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showInfoDialog("Percent of endurance runs...", "...(non-initial) which ended unintentionally and a personal best was broken:\n\n" +
                        "The percent of your unintentionally-ended endurance runs(not counting initial runs), in which you broke a previously" +
                        " set personal best record.  To find this number, the following formula was used; 100*(runs in" +
                        " which you unintentionally-ended and broke a personal record / all non-initial, unintentionally-ended, endurance runs)");
                return true;
            }
        });
        statsPercentEndurUnintentionalEndPersonalBestBrokeNum = (TextView) findViewById(R.id.statsPercentEndurUnintentionalEndPersonalBestBrokeNum);
        statsPercentEndurEndSpacer = (TextView) findViewById(R.id.statsPercentEndurEndSpacer);

        statsCurrentStreakLabel = (TextView) findViewById(R.id.statsCurrentStreakLabel);
        statsCurrentStreakLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                List<TextView> textViewList = new ArrayList<TextView>();
                textViewList.add(statsCurrentStreakPersonalBestLabel);
                textViewList.add(statsCurrentStreakPersonalBestNum);
                textViewList.add(statsCurrentStreakPersonalBestIntentionalEndLabel);
                textViewList.add(statsCurrentStreakPersonalBestIntentionalEndNum);
                textViewList.add(statsCurrentStreakPersonalBestUnintentionalEndLabel);
                textViewList.add(statsCurrentStreakPersonalBestUnintentionalEndNum);
                textViewList.add(statsCurrentStreakEndSpacer);


                statsToggleTextViewVisibility(statsCurrentStreakLabel, textViewList);

            }
        });
        statsCurrentStreakLabel.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showInfoDialog("Current streak of consecutive endurance runs...", "These will show you your current streak of" +
                        " endurance runs in which you have done the following things.");
                return true;
            }
        });
        statsCurrentStreakPersonalBestLabel = (TextView) findViewById(R.id.statsCurrentStreakPersonalBestLabel);
        statsCurrentStreakPersonalBestLabel.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showInfoDialog("Current streak of consecutive endurance runs...", "...in which a personal best was broken (ignoring initial runs)\n\n" +
                        "This is the number of runs in a row that you have broken a personal best leading up to right now." +
                        "  Initial runs are ignored when calculating this streak.");
                return true;
            }
        });
        statsCurrentStreakPersonalBestNum = (TextView) findViewById(R.id.statsCurrentStreakPersonalBestNum);
        statsCurrentStreakPersonalBestIntentionalEndLabel = (TextView) findViewById(R.id.statsCurrentStreakPersonalBestIntentionalEndLabel);
        statsCurrentStreakPersonalBestIntentionalEndLabel.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showInfoDialog("Current streak of consecutive endurance runs...", "...that ended intentionally, in which a personal best was " +
                        "broken (ignoring initial runs)\n\n" +
                        "This is the number of intentionally-ended runs in a row that you have broken a personal best leading up to right now." +
                        "  Initial runs are ignored when calculating this streak.");
                return true;
            }
        });
        statsCurrentStreakPersonalBestIntentionalEndNum = (TextView) findViewById(R.id.statsCurrentStreakPersonalBestIntentionalEndNum);
        statsCurrentStreakPersonalBestUnintentionalEndLabel = (TextView) findViewById(R.id.statsCurrentStreakPersonalBestUnintentionalEndLabel);
        statsCurrentStreakPersonalBestUnintentionalEndLabel.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showInfoDialog("Current streak of consecutive endurance runs...", "...that ended unintentionally, in which a personal best was " +
                        "broken (ignoring initial runs)\n\n" +
                        "This is the number of unintentionally-ended runs in a row that you have broken a personal best leading up to right now" +
                        "  Initial runs are ignored when calculating this streak.");
                return true;
            }
        });
        statsCurrentStreakPersonalBestUnintentionalEndNum = (TextView) findViewById(R.id.statsCurrentStreakPersonalBestUnintentionalEndNum);
        statsCurrentStreakEndSpacer = (TextView) findViewById(R.id.statsCurrentStreakEndSpacer);

        statsLongestStreakLabel = (TextView) findViewById(R.id.statsLongestStreakLabel);
        statsLongestStreakLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                List<TextView> textViewList = new ArrayList<TextView>();
                textViewList.add(statsLongestStreakPersonalBestLabel);
                textViewList.add(statsLongestStreakPersonalBestNum);
                textViewList.add(statsLongestStreakPersonalBestIntentionalEndLabel);
                textViewList.add(statsLongestStreakPersonalBestIntentionalEndNum);
                textViewList.add(statsLongestStreakPersonalBestUnintentionalEndLabel);
                textViewList.add(statsLongestStreakPersonalBestUnintentionalEndNum);
                textViewList.add(statsLongestStreakEndSpacer);

                statsToggleTextViewVisibility(statsLongestStreakLabel, textViewList);

            }
        });
        statsLongestStreakLabel.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showInfoDialog("Longest streak of consecutive endurance runs...", "These will show you your longest streak of" +
                        " endurance runs in which you have done the following things.");
                return true;
            }
        });
        statsLongestStreakPersonalBestLabel = (TextView) findViewById(R.id.statsLongestStreakPersonalBestLabel);
        statsLongestStreakPersonalBestLabel.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showInfoDialog("Longest streak of consecutive endurance runs...", "...in which a personal best was broken (ignoring initial runs)\n\n" +
                        "This is the longest streak of runs in which you have broken a personal bests." +
                        "  Initial runs are ignored when calculating this streak.");
                return true;
            }
        });
        statsLongestStreakPersonalBestNum = (TextView) findViewById(R.id.statsLongestStreakPersonalBestNum);
        statsLongestStreakPersonalBestIntentionalEndLabel = (TextView) findViewById(R.id.statsLongestStreakPersonalBestIntentionalEndLabel);
        statsLongestStreakPersonalBestIntentionalEndLabel.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showInfoDialog("Longest streak of consecutive endurance runs...", "...that ended intentionally, in which a personal best was " +
                        "broken (ignoring initial runs)\n\n" +
                        "This is the longest streak of intentionally-ended runs in which you have broken a personal best." +
                        "  Initial runs are ignored when calculating this streak.");

                return true;
            }
        });
        statsLongestStreakPersonalBestIntentionalEndNum = (TextView) findViewById(R.id.statsLongestStreakPersonalBestIntentionalEndNum);
        statsLongestStreakPersonalBestUnintentionalEndLabel = (TextView) findViewById(R.id.statsLongestStreakPersonalBestUnintentionalEndLabel);
        statsLongestStreakPersonalBestUnintentionalEndLabel.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showInfoDialog("Longest streak of consecutive endurance runs...", "...that ended unintentionally, in which a personal best was " +
                        "broken (ignoring initial runs)\n\n" +
                        "This is the longest streak of unintentionally-ended runs in which you have broken a personal best." +
                        "  Initial runs are ignored when calculating this streak.");

                return true;
            }
        });
        statsLongestStreakPersonalBestUnintentionalEndNum = (TextView) findViewById(R.id.statsLongestStreakPersonalBestUnintentionalEndNum);
        statsLongestStreakEndSpacer = (TextView) findViewById(R.id.statsLongestStreakEndSpacer);

        statsNumberOfDaysDoingAtLeastXBlankEveryYDaysLabel = (TextView) findViewById(R.id.statsNumberOfDaysDoingAtLeastXBlankEveryYDaysLabel);
        statsNumberOfDaysDoingAtLeastXBlankEveryYDaysLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                List<EditText> editTextList = new ArrayList<EditText>();
                editTextList.add(statsNumberOfDaysDoingAtLeastXBlankEveryYDaysXEditText);
                editTextList.add(statsNumberOfDaysDoingAtLeastXBlankEveryYDaysYEditText);
                statsToggleEditTextVisibility(editTextList);

                List<Button> ButtonList = new ArrayList<Button>();
                ButtonList.add(statsNumberOfDaysDoingAtLeastXBlankEveryYDaysXEditTextButton);
                ButtonList.add(statsNumberOfDaysDoingAtLeastXBlankEveryYDaysYEditTextButton);
                statsToggleButtonVisibility(ButtonList);

                List<TextView> textViewList = new ArrayList<TextView>();
                textViewList.add(statsNumberOfDaysDoingAtLeastXBlankEveryYDaysDefineXLabel);
                textViewList.add(statsNumberOfDaysDoingAtLeastXBlankEveryYDaysDefineYLabel);
                textViewList.add(statsNumberOfDaysDoingAtLeastXBlankEveryYDaysRunLabel);
                textViewList.add(statsNumberOfDaysDoingAtLeastXBlankEveryYDaysRunNum);
                textViewList.add(statsNumberOfDaysDoingAtLeastXBlankEveryYDaysEnduranceRunLabel);
                textViewList.add(statsNumberOfDaysDoingAtLeastXBlankEveryYDaysEnduranceRunNum);
                textViewList.add(statsNumberOfDaysDoingAtLeastXBlankEveryYDaysEnduranceRunPersonalBestLabel);
                textViewList.add(statsNumberOfDaysDoingAtLeastXBlankEveryYDaysEnduranceRunPersonalBestNum);
                textViewList.add(statsNumberOfDaysDoingAtLeastXBlankEveryYDaysEnduranceRunIntentionalEndLabel);
                textViewList.add(statsNumberOfDaysDoingAtLeastXBlankEveryYDaysEnduranceRunIntentionalEndNum);
                textViewList.add(statsNumberOfDaysDoingAtLeastXBlankEveryYDaysEnduranceRunIntentionalEndPersonalBestLabel);
                textViewList.add(statsNumberOfDaysDoingAtLeastXBlankEveryYDaysEnduranceRunIntentionalEndPersonalBestNum);
                textViewList.add(statsNumberOfDaysDoingAtLeastXBlankEveryYDaysEnduranceRunUnintentionalEndLabel);
                textViewList.add(statsNumberOfDaysDoingAtLeastXBlankEveryYDaysEnduranceRunUnintentionalEndNum);
                textViewList.add(statsNumberOfDaysDoingAtLeastXBlankEveryYDaysEnduranceRunUnintentionalEndPersonalBestLabel);
                textViewList.add(statsNumberOfDaysDoingAtLeastXBlankEveryYDaysEnduranceRunUnintentionalEndPersonalBestNum);
                textViewList.add(statsNumberOfDaysDoingAtLeastXBlankEveryYDaysInitialEnduranceLabel);
                textViewList.add(statsNumberOfDaysDoingAtLeastXBlankEveryYDaysInitialEnduranceNum);
                textViewList.add(statsNumberOfDaysDoingAtLeastXBlankEveryYDaysDrillRunLabel);
                textViewList.add(statsNumberOfDaysDoingAtLeastXBlankEveryYDaysDrillRunNum);
                textViewList.add(statsNumberOfDaysDoingAtLeastXBlankEveryYDaysDrillRunRestartLabel);
                textViewList.add(statsNumberOfDaysDoingAtLeastXBlankEveryYDaysDrillRunRestartNum);
                textViewList.add(statsNumberOfDaysDoingAtLeastXBlankEveryYDaysDrillRunRestartDroplessLabel);
                textViewList.add(statsNumberOfDaysDoingAtLeastXBlankEveryYDaysDrillRunRestartDroplessNum);
                textViewList.add(statsNumberOfDaysDoingAtLeastXBlankEveryYDaysDrillRunDontRestartLabel);
                textViewList.add(statsNumberOfDaysDoingAtLeastXBlankEveryYDaysDrillRunDontRestartNum);
                textViewList.add(statsNumberOfDaysDoingAtLeastXBlankEveryYDaysInitialDrillRunLabel);
                textViewList.add(statsNumberOfDaysDoingAtLeastXBlankEveryYDaysInitialDrillRunNum);
                textViewList.add(statsNumberOfDaysDoingAtLeastXBlankEveryYDaysEndSpacer);


                statsToggleTextViewVisibility(statsNumberOfDaysDoingAtLeastXBlankEveryYDaysLabel, textViewList);

            }
        });
        statsNumberOfDaysDoingAtLeastXBlankEveryYDaysLabel.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showInfoDialog("Number of days, up until today, doing at least X ... every Y days", "This will show you your current streak as" +
                        " defined by you using X and Y doing the following things. For example if you wanted to see how many weeks you have gone" +
                        " doing at least 50 endurance runs, then define X as 50 and Y as 7.");
                return true;
            }
        });
        statsNumberOfDaysDoingAtLeastXBlankEveryYDaysDefineXLabel = (TextView) findViewById(R.id.statsNumberOfDaysDoingAtLeastXBlankEveryYDaysDefineXLabel);
        statsNumberOfDaysDoingAtLeastXBlankEveryYDaysXEditText = (EditText) findViewById(R.id.statsNumberOfDaysDoingAtLeastXBlankEveryYDaysXEditText);
        statsNumberOfDaysDoingAtLeastXBlankEveryYDaysDefineYLabel = (TextView) findViewById(R.id.statsNumberOfDaysDoingAtLeastXBlankEveryYDaysDefineYLabel);
        statsNumberOfDaysDoingAtLeastXBlankEveryYDaysYEditText = (EditText) findViewById(R.id.statsNumberOfDaysDoingAtLeastXBlankEveryYDaysYEditText);
        statsNumberOfDaysDoingAtLeastXBlankEveryYDaysXEditTextButton = (Button) findViewById(R.id.statsNumberOfDaysDoingAtLeastXBlankEveryYDaysXEditTextButton);
        statsNumberOfDaysDoingAtLeastXBlankEveryYDaysXEditTextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refreshStats();
            }
        });
        statsNumberOfDaysDoingAtLeastXBlankEveryYDaysYEditTextButton = (Button) findViewById(R.id.statsNumberOfDaysDoingAtLeastXBlankEveryYDaysYEditTextButton);
        statsNumberOfDaysDoingAtLeastXBlankEveryYDaysYEditTextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refreshStats();
            }
        });
        statsNumberOfDaysDoingAtLeastXBlankEveryYDaysRunLabel = (TextView) findViewById(R.id.statsNumberOfDaysDoingAtLeastXBlankEveryYDaysRunLabel);
        statsNumberOfDaysDoingAtLeastXBlankEveryYDaysRunLabel.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showInfoDialog("Number of days, up until today, doing at least X ...", "...run/s every Y days:\n\n" +
                        "The number given is based on what you have input as X and Y.");
                return true;
            }
        });
        statsNumberOfDaysDoingAtLeastXBlankEveryYDaysRunNum = (TextView) findViewById(R.id.statsNumberOfDaysDoingAtLeastXBlankEveryYDaysRunNum);
        statsNumberOfDaysDoingAtLeastXBlankEveryYDaysEnduranceRunLabel = (TextView) findViewById(R.id.statsNumberOfDaysDoingAtLeastXBlankEveryYDaysEnduranceRunLabel);
        statsNumberOfDaysDoingAtLeastXBlankEveryYDaysEnduranceRunLabel.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showInfoDialog("Number of days, up until today, doing at least X...", "...endurance run/s every Y days:\n\n" +
                        "The number given is based on what you have input as X and Y.");
                return true;
            }
        });
        statsNumberOfDaysDoingAtLeastXBlankEveryYDaysEnduranceRunNum = (TextView) findViewById(R.id.statsNumberOfDaysDoingAtLeastXBlankEveryYDaysEnduranceRunNum);
        statsNumberOfDaysDoingAtLeastXBlankEveryYDaysEnduranceRunPersonalBestLabel = (TextView) findViewById(R.id.statsNumberOfDaysDoingAtLeastXBlankEveryYDaysEnduranceRunPersonalBestLabel);
        statsNumberOfDaysDoingAtLeastXBlankEveryYDaysEnduranceRunPersonalBestLabel.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showInfoDialog("Number of days, up until today, doing at least X...", "...endurance run/s where a personal best was broken every Y days:\n\n" +
                        "The number given is based on what you have input as X and Y.");
                return true;
            }
        });
        statsNumberOfDaysDoingAtLeastXBlankEveryYDaysEnduranceRunPersonalBestNum = (TextView) findViewById(R.id.statsNumberOfDaysDoingAtLeastXBlankEveryYDaysEnduranceRunPersonalBestNum);
        statsNumberOfDaysDoingAtLeastXBlankEveryYDaysEnduranceRunIntentionalEndLabel = (TextView) findViewById(R.id.statsNumberOfDaysDoingAtLeastXBlankEveryYDaysEnduranceRunIntentionalEndLabel);
        statsNumberOfDaysDoingAtLeastXBlankEveryYDaysEnduranceRunIntentionalEndLabel.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showInfoDialog("Number of days, up until today, doing at least X...", "...endurance run/s that ended intentionally every Y days:\n\n" +
                        "The number given is based on what you have input as X and Y.");
                return true;
            }
        });
        statsNumberOfDaysDoingAtLeastXBlankEveryYDaysEnduranceRunIntentionalEndNum = (TextView) findViewById(R.id.statsNumberOfDaysDoingAtLeastXBlankEveryYDaysEnduranceRunIntentionalEndNum);
        statsNumberOfDaysDoingAtLeastXBlankEveryYDaysEnduranceRunIntentionalEndPersonalBestLabel = (TextView) findViewById(R.id.statsNumberOfDaysDoingAtLeastXBlankEveryYDaysEnduranceRunIntentionalEndPersonalBestLabel);
        statsNumberOfDaysDoingAtLeastXBlankEveryYDaysEnduranceRunIntentionalEndPersonalBestLabel.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showInfoDialog("Number of days, up until today, doing at least X...", "...endurance runs that ended intentionally where a personal best was broken run/s every Y days:\n\n" +
                        "The number given is based on what you have input as X and Y.");
                return true;
            }
        });
        statsNumberOfDaysDoingAtLeastXBlankEveryYDaysEnduranceRunIntentionalEndPersonalBestNum = (TextView) findViewById(R.id.statsNumberOfDaysDoingAtLeastXBlankEveryYDaysEnduranceRunIntentionalEndPersonalBestNum);
        statsNumberOfDaysDoingAtLeastXBlankEveryYDaysEnduranceRunUnintentionalEndLabel = (TextView) findViewById(R.id.statsNumberOfDaysDoingAtLeastXBlankEveryYDaysEnduranceRunUnintentionalEndLabel);
        statsNumberOfDaysDoingAtLeastXBlankEveryYDaysEnduranceRunUnintentionalEndLabel.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showInfoDialog("Number of days, up until today, doing at least X...", "...endurance run/s that ended unintentionally every Y days:\n\n" +
                        "The number given is based on what you have input as X and Y.");
                return true;
            }
        });
        statsNumberOfDaysDoingAtLeastXBlankEveryYDaysEnduranceRunUnintentionalEndNum = (TextView) findViewById(R.id.statsNumberOfDaysDoingAtLeastXBlankEveryYDaysEnduranceRunUnintentionalEndNum);
        statsNumberOfDaysDoingAtLeastXBlankEveryYDaysEnduranceRunUnintentionalEndPersonalBestLabel = (TextView) findViewById(R.id.statsNumberOfDaysDoingAtLeastXBlankEveryYDaysEnduranceRunUnintentionalEndPersonalBestLabel);
        statsNumberOfDaysDoingAtLeastXBlankEveryYDaysEnduranceRunUnintentionalEndPersonalBestLabel.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showInfoDialog("Number of days, up until today, doing at least X...", "...endurance runs that ended unintentionally where a personal best was broken run/s every Y days:\n\n" +
                        "The number given is based on what you have input as X and Y.");
                return true;
            }
        });
        statsNumberOfDaysDoingAtLeastXBlankEveryYDaysEnduranceRunUnintentionalEndPersonalBestNum = (TextView) findViewById(R.id.statsNumberOfDaysDoingAtLeastXBlankEveryYDaysEnduranceRunUnintentionalEndPersonalBestNum);
        statsNumberOfDaysDoingAtLeastXBlankEveryYDaysInitialEnduranceLabel = (TextView) findViewById(R.id.statsNumberOfDaysDoingAtLeastXBlankEveryYDaysInitialEnduranceLabel);
        statsNumberOfDaysDoingAtLeastXBlankEveryYDaysInitialEnduranceLabel.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showInfoDialog("Number of days, up until today, doing at least X...", "...initial endurance run/s every Y days:\n\n" +
                        "The number given is based on what you have input as X and Y.");
                return true;
            }
        });
        statsNumberOfDaysDoingAtLeastXBlankEveryYDaysInitialEnduranceNum = (TextView) findViewById(R.id.statsNumberOfDaysDoingAtLeastXBlankEveryYDaysInitialEnduranceNum);
        statsNumberOfDaysDoingAtLeastXBlankEveryYDaysDrillRunLabel = (TextView) findViewById(R.id.statsNumberOfDaysDoingAtLeastXBlankEveryYDaysDrillRunLabel);
        statsNumberOfDaysDoingAtLeastXBlankEveryYDaysDrillRunLabel.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showInfoDialog("Number of days, up until today, doing at least X...", "...drill run/s every Y days:\n\n" +
                        "The number given is based on what you have input as X and Y.");
                return true;
            }
        });
        statsNumberOfDaysDoingAtLeastXBlankEveryYDaysDrillRunNum = (TextView) findViewById(R.id.statsNumberOfDaysDoingAtLeastXBlankEveryYDaysDrillRunNum);
        statsNumberOfDaysDoingAtLeastXBlankEveryYDaysDrillRunRestartLabel = (TextView) findViewById(R.id.statsNumberOfDaysDoingAtLeastXBlankEveryYDaysDrillRunRestartLabel);
        statsNumberOfDaysDoingAtLeastXBlankEveryYDaysDrillRunRestartLabel.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showInfoDialog("Number of days, up until today, doing at least X...", "...drill run/s in \"restart set on unintentionalEnds\" mode every Y days:\n\n" +
                        "The number given is based on what you have input as X and Y.");
                return true;
            }
        });
        statsNumberOfDaysDoingAtLeastXBlankEveryYDaysDrillRunRestartNum = (TextView) findViewById(R.id.statsNumberOfDaysDoingAtLeastXBlankEveryYDaysDrillRunRestartNum);
        statsNumberOfDaysDoingAtLeastXBlankEveryYDaysDrillRunRestartDroplessLabel = (TextView) findViewById(R.id.statsNumberOfDaysDoingAtLeastXBlankEveryYDaysDrillRunRestartDroplessLabel);
        statsNumberOfDaysDoingAtLeastXBlankEveryYDaysDrillRunRestartDroplessLabel.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showInfoDialog("Number of days, up until today, doing at least X...", "...drill run/s in \"restart set on unintentionalEnds\" mode that was dropless every Y days:\n\n" +
                        "The number given is based on what you have input as X and Y.");
                return true;
            }
        });
        statsNumberOfDaysDoingAtLeastXBlankEveryYDaysDrillRunRestartDroplessNum = (TextView) findViewById(R.id.statsNumberOfDaysDoingAtLeastXBlankEveryYDaysDrillRunRestartDroplessNum);
        statsNumberOfDaysDoingAtLeastXBlankEveryYDaysDrillRunDontRestartLabel = (TextView) findViewById(R.id.statsNumberOfDaysDoingAtLeastXBlankEveryYDaysDrillRunDontRestartLabel);
        statsNumberOfDaysDoingAtLeastXBlankEveryYDaysDrillRunDontRestartLabel.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showInfoDialog("Number of days, up until today, doing at least X...", "...drill run/s in \"don't restart set on unintentionalEnds\" mode every Y days:\n\n" +
                        "The number given is based on what you have input as X and Y.");
                return true;
            }
        });
        statsNumberOfDaysDoingAtLeastXBlankEveryYDaysDrillRunDontRestartNum = (TextView) findViewById(R.id.statsNumberOfDaysDoingAtLeastXBlankEveryYDaysDrillRunDontRestartNum);
        statsNumberOfDaysDoingAtLeastXBlankEveryYDaysInitialDrillRunLabel = (TextView) findViewById(R.id.statsNumberOfDaysDoingAtLeastXBlankEveryYDaysInitialDrillRunLabel);
        statsNumberOfDaysDoingAtLeastXBlankEveryYDaysInitialDrillRunLabel.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showInfoDialog("Number of days, up until today, doing at least X...", "...initial drill run/s every Y days:\n\n" +
                        "The number given is based on what you have input as X and Y.");
                return true;
            }
        });
        statsNumberOfDaysDoingAtLeastXBlankEveryYDaysInitialDrillRunNum = (TextView) findViewById(R.id.statsNumberOfDaysDoingAtLeastXBlankEveryYDaysInitialDrillRunNum);
        statsNumberOfDaysDoingAtLeastXBlankEveryYDaysEndSpacer = (TextView) findViewById(R.id.statsNumberOfDaysDoingAtLeastXBlankEveryYDaysEndSpacer);

        statsMostDaysDoingAtLeastXBlankEveryYDaysLabel = (TextView) findViewById(R.id.statsMostDaysDoingAtLeastXBlankEveryYDaysLabel);
        statsMostDaysDoingAtLeastXBlankEveryYDaysLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                List<EditText> editTextList = new ArrayList<EditText>();
                editTextList.add(statsMostDaysDoingAtLeastXBlankEveryYDaysXEditText);
                editTextList.add(statsMostDaysDoingAtLeastXBlankEveryYDaysYEditText);
                statsToggleEditTextVisibility(editTextList);

                List<Button> ButtonList = new ArrayList<Button>();
                ButtonList.add(statsMostDaysDoingAtLeastXBlankEveryYDaysXEditTextButton);
                ButtonList.add(statsMostDaysDoingAtLeastXBlankEveryYDaysYEditTextButton);
                statsToggleButtonVisibility(ButtonList);

                List<TextView> textViewList = new ArrayList<TextView>();

                textViewList.add(statsMostDaysDoingAtLeastXBlankEveryYDaysDefineXLabel);
                textViewList.add(statsMostDaysDoingAtLeastXBlankEveryYDaysDefineYLabel);
                textViewList.add(statsMostDaysDoingAtLeastXBlankEveryYDaysRunLabel);
                textViewList.add(statsMostDaysDoingAtLeastXBlankEveryYDaysRunNum);
                textViewList.add(statsMostDaysDoingAtLeastXBlankEveryYDaysEnduranceRunLabel);
                textViewList.add(statsMostDaysDoingAtLeastXBlankEveryYDaysEnduranceRunNum);
                textViewList.add(statsMostDaysDoingAtLeastXBlankEveryYDaysEnduranceRunPersonalBestLabel);
                textViewList.add(statsMostDaysDoingAtLeastXBlankEveryYDaysEnduranceRunPersonalBestNum);
                textViewList.add(statsMostDaysDoingAtLeastXBlankEveryYDaysEnduranceRunIntentionalEndLabel);
                textViewList.add(statsMostDaysDoingAtLeastXBlankEveryYDaysEnduranceRunIntentionalEndNum);
                textViewList.add(statsMostDaysDoingAtLeastXBlankEveryYDaysEnduranceRunIntentionalEndPersonalBestLabel);
                textViewList.add(statsMostDaysDoingAtLeastXBlankEveryYDaysEnduranceRunIntentionalEndPersonalBestNum);
                textViewList.add(statsMostDaysDoingAtLeastXBlankEveryYDaysEnduranceRunUnintentionalEndLabel);
                textViewList.add(statsMostDaysDoingAtLeastXBlankEveryYDaysEnduranceRunUnintentionalEndNum);
                textViewList.add(statsMostDaysDoingAtLeastXBlankEveryYDaysEnduranceRunUnintentionalEndPersonalBestLabel);
                textViewList.add(statsMostDaysDoingAtLeastXBlankEveryYDaysEnduranceRunUnintentionalEndPersonalBestNum);
                textViewList.add(statsMostDaysDoingAtLeastXBlankEveryYDaysInitialEnduranceLabel);
                textViewList.add(statsMostDaysDoingAtLeastXBlankEveryYDaysInitialEnduranceNum);
                textViewList.add(statsMostDaysDoingAtLeastXBlankEveryYDaysDrillRunLabel);
                textViewList.add(statsMostDaysDoingAtLeastXBlankEveryYDaysDrillRunNum);
                textViewList.add(statsMostDaysDoingAtLeastXBlankEveryYDaysDrillRunRestartLabel);
                textViewList.add(statsMostDaysDoingAtLeastXBlankEveryYDaysDrillRunRestartNum);
                textViewList.add(statsMostDaysDoingAtLeastXBlankEveryYDaysDrillRunRestartDroplessLabel);
                textViewList.add(statsMostDaysDoingAtLeastXBlankEveryYDaysDrillRunRestartDroplessNum);
                textViewList.add(statsMostDaysDoingAtLeastXBlankEveryYDaysDrillRunDontRestartLabel);
                textViewList.add(statsMostDaysDoingAtLeastXBlankEveryYDaysDrillRunDontRestartNum);
                textViewList.add(statsMostDaysDoingAtLeastXBlankEveryYDaysInitialDrillRunLabel);
                textViewList.add(statsMostDaysDoingAtLeastXBlankEveryYDaysInitialDrillRunNum);
                textViewList.add(statsMostDaysDoingAtLeastXBlankEveryYDaysEndSpacer);

                statsToggleTextViewVisibility(statsMostDaysDoingAtLeastXBlankEveryYDaysLabel, textViewList);

            }
        });
        statsMostDaysDoingAtLeastXBlankEveryYDaysLabel.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showInfoDialog("Most days ever doing at least X ... every Y days", "This uses the numbers defined as X and Y to get the longest streak" +
                        " that you have done for the following things.");
                return true;
            }
        });
        statsMostDaysDoingAtLeastXBlankEveryYDaysDefineXLabel = (TextView) findViewById(R.id.statsMostDaysDoingAtLeastXBlankEveryYDaysDefineXLabel);
        statsMostDaysDoingAtLeastXBlankEveryYDaysXEditText = (EditText) findViewById(R.id.statsMostDaysDoingAtLeastXBlankEveryYDaysXEditText);
        statsMostDaysDoingAtLeastXBlankEveryYDaysDefineYLabel = (TextView) findViewById(R.id.statsMostDaysDoingAtLeastXBlankEveryYDaysDefineYLabel);
        statsMostDaysDoingAtLeastXBlankEveryYDaysYEditText = (EditText) findViewById(R.id.statsMostDaysDoingAtLeastXBlankEveryYDaysYEditText);
        statsMostDaysDoingAtLeastXBlankEveryYDaysXEditTextButton = (Button) findViewById(R.id.statsMostDaysDoingAtLeastXBlankEveryYDaysXEditTextButton);
        statsMostDaysDoingAtLeastXBlankEveryYDaysXEditTextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refreshStats();
            }
        });
        statsMostDaysDoingAtLeastXBlankEveryYDaysYEditTextButton = (Button) findViewById(R.id.statsMostDaysDoingAtLeastXBlankEveryYDaysYEditTextButton);
        statsMostDaysDoingAtLeastXBlankEveryYDaysYEditTextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refreshStats();
            }
        });
        statsMostDaysDoingAtLeastXBlankEveryYDaysRunLabel = (TextView) findViewById(R.id.statsMostDaysDoingAtLeastXBlankEveryYDaysRunLabel);
        statsMostDaysDoingAtLeastXBlankEveryYDaysRunLabel.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showInfoDialog("Most days ever doing at least X...", "...run/s\n\n" +
                        "The number given is based on what you have input as X and Y.");
                return true;
            }
        });
        statsMostDaysDoingAtLeastXBlankEveryYDaysRunNum = (TextView) findViewById(R.id.statsMostDaysDoingAtLeastXBlankEveryYDaysRunNum);
        statsMostDaysDoingAtLeastXBlankEveryYDaysEnduranceRunLabel = (TextView) findViewById(R.id.statsMostDaysDoingAtLeastXBlankEveryYDaysEnduranceRunLabel);
        statsMostDaysDoingAtLeastXBlankEveryYDaysEnduranceRunLabel.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showInfoDialog("Most days ever doing at least X...", "...run/s every Y days:\n\n" +
                        "The number given is based on what you have input as X and Y.");
                return true;
            }
        });
        statsMostDaysDoingAtLeastXBlankEveryYDaysEnduranceRunNum = (TextView) findViewById(R.id.statsMostDaysDoingAtLeastXBlankEveryYDaysEnduranceRunNum);
        statsMostDaysDoingAtLeastXBlankEveryYDaysEnduranceRunPersonalBestLabel = (TextView) findViewById(R.id.statsMostDaysDoingAtLeastXBlankEveryYDaysEnduranceRunPersonalBestLabel);
        statsMostDaysDoingAtLeastXBlankEveryYDaysEnduranceRunPersonalBestLabel.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showInfoDialog("Most days ever doing at least X...", "...endurance run/s every Y days:\n\n" +
                        "The number given is based on what you have input as X and Y.");
                return true;
            }
        });
        statsMostDaysDoingAtLeastXBlankEveryYDaysEnduranceRunPersonalBestNum = (TextView) findViewById(R.id.statsMostDaysDoingAtLeastXBlankEveryYDaysEnduranceRunPersonalBestNum);
        statsMostDaysDoingAtLeastXBlankEveryYDaysEnduranceRunIntentionalEndLabel = (TextView) findViewById(R.id.statsMostDaysDoingAtLeastXBlankEveryYDaysEnduranceRunIntentionalEndLabel);
        statsMostDaysDoingAtLeastXBlankEveryYDaysEnduranceRunIntentionalEndLabel.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showInfoDialog("Most days ever doing at least X...", "...endurance run/s that ended intentionally every Y days:\n\n" +
                        "The number given is based on what you have input as X and Y.");
                return true;
            }
        });
        statsMostDaysDoingAtLeastXBlankEveryYDaysEnduranceRunIntentionalEndNum = (TextView) findViewById(R.id.statsMostDaysDoingAtLeastXBlankEveryYDaysEnduranceRunIntentionalEndNum);
        statsMostDaysDoingAtLeastXBlankEveryYDaysEnduranceRunIntentionalEndPersonalBestLabel = (TextView) findViewById(R.id.statsMostDaysDoingAtLeastXBlankEveryYDaysEnduranceRunIntentionalEndPersonalBestLabel);
        statsMostDaysDoingAtLeastXBlankEveryYDaysEnduranceRunIntentionalEndPersonalBestLabel.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showInfoDialog("Most days ever doing at least X...", "...endurance run/s that ended intentionally where a personal best was broken every Y days:\n\n" +
                        "The number given is based on what you have input as X and Y.");
                return true;
            }
        });
        statsMostDaysDoingAtLeastXBlankEveryYDaysEnduranceRunIntentionalEndPersonalBestNum = (TextView) findViewById(R.id.statsMostDaysDoingAtLeastXBlankEveryYDaysEnduranceRunIntentionalEndPersonalBestNum);
        statsMostDaysDoingAtLeastXBlankEveryYDaysEnduranceRunUnintentionalEndLabel = (TextView) findViewById(R.id.statsMostDaysDoingAtLeastXBlankEveryYDaysEnduranceRunUnintentionalEndLabel);
        statsMostDaysDoingAtLeastXBlankEveryYDaysEnduranceRunUnintentionalEndLabel.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showInfoDialog("Most days ever doing at least X...", "...endurance run/s that ended unintentionally every Y days:\n\n" +
                        "The number given is based on what you have input as X and Y.");
                return true;
            }
        });
        statsMostDaysDoingAtLeastXBlankEveryYDaysEnduranceRunUnintentionalEndNum = (TextView) findViewById(R.id.statsMostDaysDoingAtLeastXBlankEveryYDaysEnduranceRunUnintentionalEndNum);
        statsMostDaysDoingAtLeastXBlankEveryYDaysEnduranceRunUnintentionalEndPersonalBestLabel = (TextView) findViewById(R.id.statsMostDaysDoingAtLeastXBlankEveryYDaysEnduranceRunUnintentionalEndPersonalBestLabel);
        statsMostDaysDoingAtLeastXBlankEveryYDaysEnduranceRunUnintentionalEndPersonalBestLabel.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showInfoDialog("Most days ever doing at least X...", "...endurance run/s that ended unintentionally where a personal best was broken every Y days:\n\n" +
                        "The number given is based on what you have input as X and Y.");
                return true;
            }
        });
        statsMostDaysDoingAtLeastXBlankEveryYDaysEnduranceRunUnintentionalEndPersonalBestNum = (TextView) findViewById(R.id.statsMostDaysDoingAtLeastXBlankEveryYDaysEnduranceRunUnintentionalEndPersonalBestNum);
        statsMostDaysDoingAtLeastXBlankEveryYDaysInitialEnduranceLabel = (TextView) findViewById(R.id.statsMostDaysDoingAtLeastXBlankEveryYDaysInitialEnduranceLabel);
        statsMostDaysDoingAtLeastXBlankEveryYDaysInitialEnduranceLabel.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showInfoDialog("Most days ever doing at least X...", "...initial endurance run/s every Y days:\n\n" +
                        "The number given is based on what you have input as X and Y.");
                return true;
            }
        });
        statsMostDaysDoingAtLeastXBlankEveryYDaysInitialEnduranceNum = (TextView) findViewById(R.id.statsMostDaysDoingAtLeastXBlankEveryYDaysInitialEnduranceNum);
        statsMostDaysDoingAtLeastXBlankEveryYDaysDrillRunLabel = (TextView) findViewById(R.id.statsMostDaysDoingAtLeastXBlankEveryYDaysDrillRunLabel);
        statsMostDaysDoingAtLeastXBlankEveryYDaysDrillRunLabel.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showInfoDialog("Most days ever doing at least X...", "...drill run/s every Y days:\n\n" +
                        "The number given is based on what you have input as X and Y.");
                return true;
            }
        });
        statsMostDaysDoingAtLeastXBlankEveryYDaysDrillRunNum = (TextView) findViewById(R.id.statsMostDaysDoingAtLeastXBlankEveryYDaysDrillRunNum);
        statsMostDaysDoingAtLeastXBlankEveryYDaysDrillRunRestartLabel = (TextView) findViewById(R.id.statsMostDaysDoingAtLeastXBlankEveryYDaysDrillRunRestartLabel);
        statsMostDaysDoingAtLeastXBlankEveryYDaysDrillRunRestartLabel.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showInfoDialog("Most days ever doing at least X...", "...drill run/s in \"restart set on unintentionalEnds\" mode every Y days:\n\n" +
                        "The number given is based on what you have input as X and Y.");
                return true;
            }
        });
        statsMostDaysDoingAtLeastXBlankEveryYDaysDrillRunRestartNum = (TextView) findViewById(R.id.statsMostDaysDoingAtLeastXBlankEveryYDaysDrillRunRestartNum);
        statsMostDaysDoingAtLeastXBlankEveryYDaysDrillRunRestartDroplessLabel = (TextView) findViewById(R.id.statsMostDaysDoingAtLeastXBlankEveryYDaysDrillRunRestartDroplessLabel);
        statsMostDaysDoingAtLeastXBlankEveryYDaysDrillRunRestartDroplessLabel.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showInfoDialog("Most days ever doing at least X...", "...drill run/s in \"restart set on unintentionalEnds\" mode that was dropless every Y days:\n\n" +
                        "The number given is based on what you have input as X and Y.");
                return true;
            }
        });
        statsMostDaysDoingAtLeastXBlankEveryYDaysDrillRunRestartDroplessNum = (TextView) findViewById(R.id.statsMostDaysDoingAtLeastXBlankEveryYDaysDrillRunRestartDroplessNum);
        statsMostDaysDoingAtLeastXBlankEveryYDaysDrillRunDontRestartLabel = (TextView) findViewById(R.id.statsMostDaysDoingAtLeastXBlankEveryYDaysDrillRunDontRestartLabel);
        statsMostDaysDoingAtLeastXBlankEveryYDaysDrillRunDontRestartLabel.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showInfoDialog("Most days ever doing at least X...", "...drill run/s in \"don't restart set on unintentionalEnds\" mode every Y days:\n\n" +
                        "The number given is based on what you have input as X and Y.");
                return true;
            }
        });
        statsMostDaysDoingAtLeastXBlankEveryYDaysDrillRunDontRestartNum = (TextView) findViewById(R.id.statsMostDaysDoingAtLeastXBlankEveryYDaysDrillRunDontRestartNum);
        statsMostDaysDoingAtLeastXBlankEveryYDaysInitialDrillRunLabel = (TextView) findViewById(R.id.statsMostDaysDoingAtLeastXBlankEveryYDaysInitialDrillRunLabel);
        statsMostDaysDoingAtLeastXBlankEveryYDaysInitialDrillRunLabel.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showInfoDialog("Most days ever doing at least X...", "...initial drill run/s every Y days:\n\n" +
                        "The number given is based on what you have input as X and Y.");
                return true;
            }
        });
        statsMostDaysDoingAtLeastXBlankEveryYDaysInitialDrillRunNum = (TextView) findViewById(R.id.statsMostDaysDoingAtLeastXBlankEveryYDaysInitialDrillRunNum);
        statsMostDaysDoingAtLeastXBlankEveryYDaysEndSpacer = (TextView) findViewById(R.id.statsMostDaysDoingAtLeastXBlankEveryYDaysEndSpacer);

        statsUniqueComboModifiersAndEntriesLabel = (TextView) findViewById(R.id.statsUniqueComboModifiersAndEntriesLabel);
        statsUniqueComboModifiersAndEntriesLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                List<TextView> textViewList = new ArrayList<TextView>();
                textViewList.add(statsUniqueComboAnyLabel);
                textViewList.add(statsUniqueComboAnyNum);
                textViewList.add(statsUniqueComboEndurLabel);
                textViewList.add(statsUniqueComboEndurNum);
                textViewList.add(statsUniqueComboEndurIntentionalEndLabel);
                textViewList.add(statsUniqueComboEndurIntentionalEndNum);
                textViewList.add(statsUniqueComboEndurUnintentionalEndLabel);
                textViewList.add(statsUniqueComboEndurUnintentionalEndNum);
                textViewList.add(statsUniqueComboDrillLabel);
                textViewList.add(statsUniqueComboDrillNum);
                textViewList.add(statsUniqueComboDrillRestartLabel);
                textViewList.add(statsUniqueComboDrillRestartNum);
                textViewList.add(statsUniqueComboDrillDontRestartLabel);
                textViewList.add(statsUniqueComboDrillDontRestartNum);
                textViewList.add(statsUniqueComboEndurAndDrillLabel);
                textViewList.add(statsUniqueComboEndurAndDrillNum);
                textViewList.add(statsUniqueComboEndSpacer);

                statsToggleTextViewVisibility(statsUniqueComboModifiersAndEntriesLabel, textViewList);

            }
        });
        statsUniqueComboModifiersAndEntriesLabel.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showInfoDialog("Unique combinations of modifiers and entries with...", "This will show you how many different unique combinations" +
                        " you have used. For instance, the pattern ss:3 with no modifiers is counted seperately from ss:3 with the 'overhead' modifier.");
                return true;
            }
        });
        statsUniqueComboAnyLabel = (TextView) findViewById(R.id.statsUniqueComboAnyLabel);
        statsUniqueComboAnyLabel.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showInfoDialog("Unique combinations of modifiers and entries with...", "...any history:");
                return true;
            }
        });
        statsUniqueComboAnyNum = (TextView) findViewById(R.id.statsUniqueComboAnyNum);
        statsUniqueComboEndurLabel = (TextView) findViewById(R.id.statsUniqueComboEndurLabel);
        statsUniqueComboEndurLabel.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showInfoDialog("Unique combinations of modifiers and entries with...", "...endurance history:");
                return true;
            }
        });
        statsUniqueComboEndurNum = (TextView) findViewById(R.id.statsUniqueComboEndurNum);
        statsUniqueComboEndurIntentionalEndLabel = (TextView) findViewById(R.id.statsUniqueComboEndurIntentionalEndLabel);
        statsUniqueComboEndurIntentionalEndLabel.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showInfoDialog("Unique combinations of modifiers and entries with...", "...endurance history that ended intentionally:");
                return true;
            }
        });
        statsUniqueComboEndurIntentionalEndNum = (TextView) findViewById(R.id.statsUniqueComboEndurIntentionalEndNum);
        statsUniqueComboEndurUnintentionalEndLabel = (TextView) findViewById(R.id.statsUniqueComboEndurUnintentionalEndLabel);
        statsUniqueComboEndurUnintentionalEndLabel.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showInfoDialog("Unique combinations of modifiers and entries with...", "...endurance history that ended unintentionally:");
                return true;
            }
        });
        statsUniqueComboEndurUnintentionalEndNum = (TextView) findViewById(R.id.statsUniqueComboEndurUnintentionalEndNum);
        statsUniqueComboDrillLabel = (TextView) findViewById(R.id.statsUniqueComboDrillLabel);
        statsUniqueComboDrillLabel.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showInfoDialog("Unique combinations of modifiers and entries with...", "...drill history:");
                return true;
            }
        });
        statsUniqueComboDrillNum = (TextView) findViewById(R.id.statsUniqueComboDrillNum);
        statsUniqueComboDrillRestartLabel = (TextView) findViewById(R.id.statsUniqueComboDrillRestartLabel);
        statsUniqueComboDrillRestartLabel.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showInfoDialog("Unique combinations of modifiers and entries with...", "...drill history in \"restart set on unintentionalEnds\" mode:");
                return true;
            }
        });
        statsUniqueComboDrillRestartNum = (TextView) findViewById(R.id.statsUniqueComboDrillRestartNum);
        statsUniqueComboDrillDontRestartLabel = (TextView) findViewById(R.id.statsUniqueComboDrillDontRestartLabel);
        statsUniqueComboDrillDontRestartLabel.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showInfoDialog("Unique combinations of modifiers and entries with...", "...drill history in \"don't restart set on unintentionalEnds\" mode:");
                return true;
            }
        });
        statsUniqueComboDrillDontRestartNum = (TextView) findViewById(R.id.statsUniqueComboDrillDontRestartNum);
        statsUniqueComboEndurAndDrillLabel = (TextView) findViewById(R.id.statsUniqueComboEndurAndDrillLabel);
        statsUniqueComboEndurAndDrillLabel.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showInfoDialog("Unique combinations of modifiers and entries with...", "...endurance and drill history:");
                return true;
            }
        });
        statsUniqueComboEndurAndDrillNum = (TextView) findViewById(R.id.statsUniqueComboEndurAndDrillNum);
        statsUniqueComboEndSpacer = (TextView) findViewById(R.id.statsUniqueComboEndSpacer);

    }

    public void statsToggleTextViewVisibility(TextView headerTV, List<TextView> textviewList) {


        //hide/reveal all of the 'statsTotal' TextViews
        if (textviewList.get(0).getVisibility() == View.VISIBLE) {
            for (TextView textviewItem : textviewList) {
                headerTV.setText(headerTV.getText().toString().replace(" -", "+"));
                textviewItem.setVisibility(View.GONE);
            }
        } else if (textviewList.get(0).getVisibility() == View.GONE) {
            for (TextView textviewItem : textviewList) {
                headerTV.setText(headerTV.getText().toString().replace("+", " -"));
                textviewItem.setVisibility(View.VISIBLE);
            }
        }

        clearFocusOfAllEditTexts();
        headerTV.requestFocus();

    }

    public void clearFocusOfAllEditTexts() {
        statsMostDaysDoingAtLeastXBlankEveryYDaysYEditText.clearFocus();
        statsMostDaysDoingAtLeastXBlankEveryYDaysXEditText.clearFocus();
        statsNumberOfDaysDoingAtLeastXBlankEveryYDaysYEditText.clearFocus();
        statsNumberOfDaysDoingAtLeastXBlankEveryYDaysXEditText.clearFocus();
        statsNumberOfBlankInLastXEditText.clearFocus();
        statsAverageNumberOfBlankPerXEditText.clearFocus();
    }

    public void statsToggleEditTextVisibility(List<EditText> editTextList) {


        //hide/reveal all of the 'statsTotal' EditTexts
        if (editTextList.get(0).getVisibility() == View.VISIBLE) {
            for (TextView textviewItem : editTextList) {
                textviewItem.setVisibility(View.GONE);
            }
        } else if (editTextList.get(0).getVisibility() == View.GONE) {
            for (TextView textviewItem : editTextList) {
                textviewItem.setVisibility(View.VISIBLE);
            }
        }


    }

    public void statsToggleButtonVisibility(List<Button> buttonList) {


        //hide/reveal all of the 'statsTotal' Buttons
        if (buttonList.get(0).getVisibility() == View.VISIBLE) {
            for (TextView textviewItem : buttonList) {
                textviewItem.setVisibility(View.GONE);
            }
        } else if (buttonList.get(0).getVisibility() == View.GONE) {
            for (TextView textviewItem : buttonList) {
                textviewItem.setVisibility(View.VISIBLE);
            }
        }


    }

    public void setVisiblityDrillSettings(int visibility) {
        runNumberPickerHours.setVisibility(visibility);
        runNumberPickerMinutes.setVisibility(visibility);
        runNumberPickerSeconds.setVisibility(visibility);
        runNumberPickerSets.setVisibility(visibility);
        runsNumberOfSetsLabel.setVisibility(visibility);
        runColonLabel1.setVisibility(visibility);
        runColonLabel2.setVisibility(visibility);
        runDrillRestartSetCheckBox.setVisibility(visibility);

    }

    int headphoneClickCounter = 0;

    public void prepareRunTabActivity() {

        runEntryName = (TextView) findViewById(R.id.runEntryName);
        runEntryName.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showInfoDialog("Selected pattern", "This is the information on the currently selected pattern in the following" +
                        " format: name / number of objects / prop type. To select a pattern, use the the" +
                        " three inputs at the top of the screen. ");
                return false;
            }
        });

        //this is the button that starts a run
        runTimerBeginButton = (Button) findViewById(R.id.runTimerBeginButton);
        runTimerBeginButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showInfoDialog("Begin a run", "This will begin a run with the pattern and modifiers selected at the top of the screen and" +
                        " the specifics of the run below.");
                return true;
            }
        });
        runTimerBeginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Log.d("runTimerBeginButton", "pressed");
                //if there is no pattern set, tell the user that in a toast and do no more
                if (patternsATV.getText().toString().matches("")) {
                    Toast.makeText(MainActivity.this, "No pattern name set.", Toast.LENGTH_LONG).show();
                    //Log.d("runTimerBeginButton", "blank");

                } else {//if there is a pattern set, then we are going to start a run

                    //Log.d("runTimerBeginButton", "notBlank");
                    updateModifierMATV();


                    //Log.d("loadEnduranceHist...", "ran");
                    loadEnduranceHistoryRecordsFromDB();

                    //we only want to start a session if the run ends up not being cancelled, so we don't know whether or not we
                    //  are going to do that, but we want to use the current time at the time of starting the run so we go ahead and keep track of
                    //  that now
                    runCurrentDateAndTimeOfMostRecentRunBegin = currentDateAndTime();

                    if (wakeLock.isHeld()) {
                        wakeLock.release();
                    }

                    //we acquire our wakeLock so that the timer keeps going even if the screen is turned off
                    if (!wakeLock.isHeld()) {
                        wakeLock.acquire();
                    }

                    //if it is set(in settings) to not let the screen turn off during a run,
                    //      then we do that here
                    if (settingsKeepScreenOnDuringRuns.isChecked()) {
                        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                    }
                    //now we check if we are going into a drill
                    if (runRadioButtonDrill.isChecked()) {

                        //since this is a drill, we check to make sure there is at least 1 set chosen
                        if (runNumberPickerSets.getValue() < 1) {
                            Toast.makeText(MainActivity.this, "Number of sets is 0.", Toast.LENGTH_LONG).show();

                        } else {
                            //this converts the time the user set for the drills to seconds
                            final int numberOfSecondsPerSet = (runNumberPickerHours.getValue() * 3600) +
                                    runNumberPickerMinutes.getValue() * 60 +
                                    runNumberPickerSeconds.getValue();
                            if (numberOfSecondsPerSet < 1) {  //user cannot do a drill if the number of seconds is set to 0

                                Toast.makeText(MainActivity.this, "Drill time is set to 00:00:00.", Toast.LENGTH_LONG).show();

                            } else {
                                //now that we know that a valid drill run is set up, we go ahead and begin the run by
                                //  setting the number of sets to 0
                                runNumberOfSetsCompleted = 0;

                                //and we want to make sure runDrillTimeSpentOnFailedAttempts is at 0 since at the begining of a run no time has been spent of
                                //  failed runs
                                runDrillTimeSpentOnFailedAttempts = 0;

                                //now we check to see which kind of drill this is, do we start over when we unintentionalEnd, or just
                                //      keep going?
                                if (runDrillRestartSetCheckBox.isChecked()) {

                                    //this is the int we will use to keep track of how many attempts the user has,
                                    //  since we are just now begining a drill run, we set it to 1
                                    runDrillAttemptCounter = 1;

                                    //since we are set to start over on unintentionalEnds,
                                    //      we go ahead and make the appropriate AlertDialog
                                    final AlertDialog alertDialogTimer = new AlertDialog.Builder(MainActivity.this).create();
                                    // alertDialog.setIcon(android.R.drawable.ic_dialog_alert);
                                    alertDialogTimer.setTitle("Drill with fatal unintentionalEnd.");
                                    alertDialogTimer.setMessage("00:00:00");
                                    alertDialogTimer.setButton(Dialog.BUTTON_POSITIVE, "UnintentionalEnd", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {


                                        }
                                    });
                                    alertDialogTimer.setOnShowListener(new DialogInterface.OnShowListener() {
                                        @Override
                                        public void onShow(DialogInterface dialog) {
                                            Button b = alertDialogTimer.getButton(AlertDialog.BUTTON_POSITIVE);
                                            b.setOnClickListener(new View.OnClickListener() {

                                                @Override
                                                public void onClick(View view) {
                                                    if (!inBuffer) {

                                                        //add one to the number of unintentionalEnds
                                                        runDrillAttemptCounter++;
                                                        runDrillTimeSpentOnFailedAttempts = runDrillTimeSpentOnFailedAttempts + timeSpentOnThisAttempt;

                                                        runsDrillTimer.cancel();


                                                        final AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
                                                        // alertDialog.setIcon(android.R.drawable.ic_dialog_alert);
                                                        alertDialog.setTitle("Set failed.");
                                                        //THIS SHOULD SHOW DRILL SUMMARY AND RESULTS
                                                        alertDialog.setMessage("Set failed, click OK to start attempt " +
                                                                Integer.toString(runDrillAttemptCounter) + " of set " + Integer.toString(runNumberOfSetsCompleted + 1)
                                                                + "/" + Integer.toString(runNumberPickerSets.getValue()) + ".");
                                                        alertDialog.setButton(Dialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
                                                            @Override
                                                            public void onClick(DialogInterface dialog, int which) {
                                                                runsDrillTimer.start();
                                                                alertDialogTimer.show();
                                                                inBuffer = true;
                                                                //Toast.makeText(MainActivity.this, "runDrillAttemptCounter = " + Integer.toString(runDrillAttemptCounter), Toast.LENGTH_LONG).show();

                                                            }

                                                        });
//                                            alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
//                                                @Override
//                                                //if we leave this dialog then we are done with the drill,
//                                                //      so we can allow the screen to be able to time out again if
//                                                //      it currently set not to
//                                                public void onDismiss(DialogInterface dialog) {
//                                                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
//                                                }
//                                            });
                                                        alertDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                                                            @Override
                                                            public void onCancel(DialogInterface dialog) {

                                                                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                                                            }
                                                        });
                                                        alertDialog.show();


                                                        //since unintentionalEnd has been clicked, we want to restart the timer at the current set,
                                                        //      it is easy to keep the same set because we havn't subtracted from it yet,
                                                        //      we just do nothing but cancel the current timer and start a new one

                                                        alertDialogTimer.cancel();
                                                    }
                                                }
                                            });
                                        }
                                    });

                                    alertDialogTimer.setButton(Dialog.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            //currentlyInDrillSet = false;
                                            currentlyInBetweenDrillSets = false;

                                            runsDrillTimer.cancel();
                                            //if the wakeLock is currently being used, we now release it
                                            if (wakeLock.isHeld()) {
                                                wakeLock.release();
                                            }
                                            //debugging
                                            //Toast.makeText(MainActivity.this, "Drill canceled5", Toast.LENGTH_LONG).show();
                                            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                                        }

                                    });
                                    alertDialogTimer.setOnCancelListener(new DialogInterface.OnCancelListener() {
                                        @Override
                                        public void onCancel(DialogInterface dialog) {
                                            //runsDrillTimer.cancel();
                                            //if the wakeLock is currently being used, we now release it
//                                            if (wakeLock.isHeld()) {
//                                                wakeLock.release();
//                                            }
                                            //debugging

                                        }
                                    });
                                    alertDialogTimer.setOnDismissListener(new DialogInterface.OnDismissListener() {
                                        @Override
                                        public void onDismiss(DialogInterface dialog) {
                                            //currentlyInDrillSet = false;
                                            currentlyInBetweenDrillSets = false;

                                            runsDrillTimer.cancel();
                                            //if the wakeLock is currently being used, we now release it
                                            if (wakeLock.isHeld()) {
                                                wakeLock.release();
                                            }
                                            Toast.makeText(MainActivity.this, "Drill canceled", Toast.LENGTH_LONG).show();
                                            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                                        }
                                    });

                                    alertDialogTimer.setOnKeyListener(new DialogInterface.OnKeyListener() {
                                        @Override
                                        public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {

//drillHeadset4

                                            if (runHeadphoneCheckBox.isChecked()) {
                                                if (keyCode == KeyEvent.KEYCODE_HEADSETHOOK) {

                                                    headphoneClickCounter++;

                                                    Handler handler = new Handler();
                                                    Runnable r = new Runnable() {

                                                        @Override
                                                        public void run() {
                                                            if (headphoneClickCounter == 2) {//'drillHeadset2'
                                                                //Log.d("headsetDrill3", "single click!");
                                                                //this makes it so that a drop is indicated
                                                                drillHeadphoneClicked = true;
                                                                //at this point we have the dialog open that says 'set failed', we need to press
                                                                //  ok in order to start the set over again


                                                            }
                                                            // double click *********************************
                                                            if (headphoneClickCounter == 4) {
                                                                //Log.d("headsetDrill3", "dbl click!");


                                                            }
                                                            if (headphoneClickCounter == 6) {
                                                                //Log.d("headsetDrill3", "trpl click!");

                                                            }
                                                            if (headphoneClickCounter == 8) {
                                                                //Log.d("headset", "4 click!");

                                                            }
                                                            headphoneClickCounter = 0;
                                                        }
                                                    };
                                                    if (headphoneClickCounter == 1) {
                                                        handler.postDelayed(r, 1500);
                                                    }
                                                    ////Log.d("headset", "pushed");
                                                    return true;

                                                    //return dialog.onKeyDown(keyCode, event);
                                                }
                                            }


                                            return false;
                                        }
                                    });

                                    alertDialogTimer.show();

                                    //this lets us know that we are at the beginning of a timer, and still in
                                    //          the buffer part(giving the user time to get ready)
                                    inBuffer = true;
                                    //this is our drillTimer
                                    runsDrillTimer = new CountDownTimer((totalNumberOfDrillSeconds() + totalBufferSeconds) * 1000, 1000) {
                                        @Override
                                        public void onTick(long millisUntilFinished) {
                                            if (inBuffer) {
                                                alertDialogTimer.setMessage("Drill will begin in " +
                                                        formattedTime(((int) millisUntilFinished / 1000) - totalNumberOfDrillSeconds()));
                                                if (((int) millisUntilFinished / 1000) - totalNumberOfDrillSeconds() == 0) {
                                                    inBuffer = false;
                                                    Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                                                    // Vibrate for 500 milliseconds
                                                    v.vibrate(500);
                                                }
                                            } else {
                                                currentlyInDrillSet = true;

                                                //this is going to count up until we get to the time specified by the user as
                                                //      the drill length
                                                alertDialogTimer.setMessage(formattedTime((int) millisUntilFinished / 1000));
                                                timeSpentOnThisAttempt = totalNumberOfDrillSeconds() - ((int) millisUntilFinished / 1000);

                                                //'drillHeadset1'
                                                if(drillHeadphoneClicked){
                                                    drillHeadphoneClicked = false;

                                                    //add one to the number of unintentionalEnds
                                                    runDrillAttemptCounter++;
                                                    runDrillTimeSpentOnFailedAttempts = runDrillTimeSpentOnFailedAttempts + timeSpentOnThisAttempt;

                                                    runsDrillTimer.cancel();


                                                    runsDrillTimer.start();
                                                    alertDialogTimer.show();
                                                    inBuffer = true;

        /*                                            final AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
                                                    // alertDialog.setIcon(android.R.drawable.ic_dialog_alert);
                                                    alertDialog.setTitle("Set failed.");
                                                    //THIS SHOULD SHOW DRILL SUMMARY AND RESULTS
                                                    alertDialog.setMessage("Set failed, click OK to start attempt " +
                                                            Integer.toString(runDrillAttemptCounter) + " of set " + Integer.toString(runNumberOfSetsCompleted + 1)
                                                            + "/" + Integer.toString(runNumberPickerSets.getValue()) + ".");
                                                    alertDialog.setButton(Dialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            runsDrillTimer.start();
                                                            alertDialogTimer.show();
                                                            inBuffer = true;
                                                            //Toast.makeText(MainActivity.this, "runDrillAttemptCounter = " + Integer.toString(runDrillAttemptCounter), Toast.LENGTH_LONG).show();

                                                        }

                                                    });
//                                            alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
//                                                @Override
//                                                //if we leave this dialog then we are done with the drill,
//                                                //      so we can allow the screen to be able to time out again if
//                                                //      it currently set not to
//                                                public void onDismiss(DialogInterface dialog) {
//                                                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
//                                                }
//                                            });
                                                    alertDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                                                        @Override
                                                        public void onCancel(DialogInterface dialog) {

                                                            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                                                        }
                                                    });
                                                    alertDialog.show();


                                                    //since unintentionalEnd has been clicked, we want to restart the timer at the current set,
                                                    //      it is easy to keep the same set because we havn't subtracted from it yet,
                                                    //      we just do nothing but cancel the current timer and start a new one

                                                    alertDialogTimer.cancel();*/
                                                }
                                            }
                                        }

                                        public void onFinish() {
                                           // currentlyInDrillSet = false;
                                           // currentlyInBetweenDrillSets = true;

                                            runNumberOfSetsCompleted++;

                                            //if the checkbox 'fatal drops' is checked, then we probably want to keep track of how many
                                            //      times it had been clicked this set so that we can keep it n the history
                                            //          -i think this should be done with an array that gets created when the drill begins

                                            //show a popup dialog saying that the either the current set is over,
                                            //      or the entire drill is over (if runNumberPickerSets = 0)
                                            if (runNumberOfSetsCompleted == runNumberPickerSets.getValue()) {//so long as this is higher than 0, there is still another set to do
                                                //but since we made it in here, it means that a drill was completed successfully and
                                                //      so we add an entry to the HISTORYDRILL Table


                                                addRunResultToHistoryDrillTableInDB();
                                                //Toast.makeText(MainActivity.this, "got here", Toast.LENGTH_LONG).show();


                                                alertDialogTimer.cancel();
//                                                //if the wakeLock is currently being used, we now release it
                                                if (wakeLock.isHeld()) {
                                                    wakeLock.release();//HERE I AM
                                                }
                                                final AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
                                                // alertDialog.setIcon(android.R.drawable.ic_dialog_alert);
                                                alertDialog.setTitle("Drill Complete");
                                                //THIS SHOULD SHOW DRILL SUMMARY AND RESULTS
                                                alertDialog.setMessage("you finished a drill");
                                                alertDialog.setButton(Dialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        alertDialog.cancel();
                                                        drillCompleted = true;
                                                        drillCompleteSound.pause();

                                                    }

                                                });

                                                alertDialog.show();
                                                if (settingsRunDrillAudioCheckBox.isChecked()) {
                                                    drillCompleteSound.setLooping(true);
                                                    drillCompleteSound.start();
                                                    Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                                                    // Vibrate for 500 milliseconds
                                                    v.vibrate(500);
                                                }
                                                //update what is shown in the history tab
                                                // loadHistoryRecordsFromDB();

                                            } else {
                                                //popup that sets "click 'begin' to begin set
                                                final AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
                                                // alertDialog.setIcon(android.R.drawable.ic_dialog_alert);
                                                alertDialog.setTitle("Set Complete");
                                                //THIS SHOULD SHOW DRILL SUMMARY AND RESULTS
                                                alertDialog.setMessage("you finished set " + runNumberOfSetsCompleted +
                                                        " out of " + (runNumberPickerSets.getValue()) + ". Click OK to begin next set.");
                                                if (settingsRunDrillAudioCheckBox.isChecked()) {
                                                    drillCompleteSound.setLooping(true);
                                                    drillCompleteSound.start();
                                                    Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                                                    // Vibrate for 500 milliseconds
                                                    v.vibrate(500);
                                                }
                                                alertDialog.setButton(Dialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        //drillCompleteSound.release();
                                                        inBuffer = true;
                                                        runsDrillTimer.start();

                                                    }

                                                });
                                                alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                                                    @Override
                                                    public void onDismiss(DialogInterface dialog) {
                                                        drillCompleteSound.pause();
                                                        //alertDialogTimer.cancel();
                                                        //if the wakeLock is currently being used, we now release it
//                                                        if (wakeLock.isHeld()) {
//                                                            wakeLock.release();
//                                                        }

                                                    }
                                                });
                                                alertDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                                                    @Override
                                                    public void onCancel(DialogInterface dialog) {
                                                        drillCompleteSound.pause();
                                                        alertDialogTimer.cancel();
                                                        //if the wakeLock is currently being used, we now release it
                                                        if (wakeLock.isHeld()) {
                                                            wakeLock.release();
                                                        }
                                                    }
                                                });
                                                alertDialog.show();

                                            }//THIS IS THE END OF THE STUFF THAT HAPPENS IF WE HAVE JUST FINISHED A SET, BUT NOT THE ENTIRE DRILL
                                        }
                                    }.start();

                                } else {//this is a drill where it doesn't matter if the user unintentionalEnds, they are not recorded,
                                    //      it is just juggle until the time is up. So we are not using attempts and so we
                                    //      set this as 0 and we will know that this record entry in the HISTORYDRILL table
                                    //      because of it
                                    runDrillAttemptCounter = 0;

                                    final AlertDialog alertDialogTimer = new AlertDialog.Builder(MainActivity.this).create();
                                    // alertDialog.setIcon(android.R.drawable.ic_dialog_alert);
                                    alertDialogTimer.setTitle("Drill without fatal unintentionalEnd");
                                    alertDialogTimer.setMessage("00:00:00");
                                    alertDialogTimer.setButton(Dialog.BUTTON_POSITIVE, "Cancel", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            runsDrillTimer.cancel();
                                            //if the wakeLock is currently being used, we now release it
                                            if (wakeLock.isHeld()) {
                                                wakeLock.release();
                                            }
                                            //Toast.makeText(MainActivity.this, "Drill canceled3", Toast.LENGTH_LONG).show();
                                            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

                                        }

                                    });
                                    alertDialogTimer.setOnCancelListener(new DialogInterface.OnCancelListener() {
                                        @Override
                                        public void onCancel(DialogInterface dialog) {
                                            runsDrillTimer.cancel();
                                            //if the wakeLock is currently being used, we now release it
                                            if (wakeLock.isHeld()) {
                                                wakeLock.release();
                                            }
                                            //Toast.makeText(MainActivity.this, "Drill canceled2", Toast.LENGTH_LONG).show();
                                            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                                        }
                                    });
                                    alertDialogTimer.setOnDismissListener(new DialogInterface.OnDismissListener() {
                                        @Override
                                        public void onDismiss(DialogInterface dialog) {
/*                                            runsDrillTimer.cancel();
                                            //if the wakeLock is currently being used, we now release it
                                            if (wakeLock.isHeld()) {
                                                wakeLock.release();
                                            }
                                            Toast.makeText(MainActivity.this, "Drill canceled2", Toast.LENGTH_LONG).show();
                                            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);*/
                                        }
                                    });

                                    alertDialogTimer.show();


                                    inBuffer = true;
                                    //this is our drillTimer
                                    runsDrillTimer = new CountDownTimer((totalNumberOfDrillSeconds() + totalBufferSeconds) * 1000, 1000) {
                                        @Override
                                        public void onTick(long millisUntilFinished) {
                                            if (inBuffer) {
                                                alertDialogTimer.setMessage("Drill will begin in " +
                                                        formattedTime(((int) millisUntilFinished / 1000) - totalNumberOfDrillSeconds()));
                                                if (((int) millisUntilFinished / 1000) - totalNumberOfDrillSeconds() == 0) {
                                                    inBuffer = false;
                                                    Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                                                    // Vibrate for 500 milliseconds
                                                    v.vibrate(500);
                                                }
                                            } else {
                                                //this is going to count up until we get to the time specified by the user as
                                                //      the drill length
                                                alertDialogTimer.setMessage(formattedTime((int) millisUntilFinished / 1000));
                                            }
                                        }

                                        public void onFinish() {
                                            runNumberOfSetsCompleted++;

                                            //if the checkbox 'fatal drops' is checked, then we probably want to keep track of how many
                                            //      times it had been clicked this set so that we can keep it n the history
                                            //          -i think this should be done with an array that gets created when the drill begins

                                            //show a popup dialog saying that the either the current set is over,
                                            //      or the entire drill is over (if runNumberPickerSets = 0)
                                            if (runNumberOfSetsCompleted == runNumberPickerSets.getValue()) {//so long as this is higher than 0, there is still another set to do

                                                //but since we made it in here, it means that a drill was completed successfully and
                                                //      so we add an entry to the HISTORYDRILL Table
                                                addRunResultToHistoryDrillTableInDB();


                                                alertDialogTimer.cancel();
                                                //if the wakeLock is currently being used, we now release it
                                                if (wakeLock.isHeld()) {
                                                    wakeLock.release();
                                                }
                                                final AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
                                                // alertDialog.setIcon(android.R.drawable.ic_dialog_alert);
                                                alertDialog.setTitle("Drill Complete");
                                                //THIS SHOULD SHOW DRILL SUMMARY AND RESULTS
                                                alertDialog.setMessage("you finished a drill");
                                                alertDialog.setButton(Dialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        alertDialog.cancel();
                                                        drillCompleted = true;
                                                        drillCompleteSound.pause();

                                                    }

                                                });

                                                alertDialog.show();
                                                if (settingsRunDrillAudioCheckBox.isChecked()) {
                                                    drillCompleteSound.setLooping(true);
                                                    drillCompleteSound.start();
                                                    Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                                                    // Vibrate for 500 milliseconds
                                                    v.vibrate(500);
                                                }
                                                //update what is shown in the history tab
                                                //  loadHistoryRecordsFromDB();

                                            } else {
                                                //popup that sets "click 'begin' to begin set
                                                final AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
                                                // alertDialog.setIcon(android.R.drawable.ic_dialog_alert);
                                                alertDialog.setTitle("Set Complete");
                                                //THIS SHOULD SHOW DRILL SUMMARY AND RESULTS
                                                alertDialog.setMessage("you finished set " + runNumberOfSetsCompleted +
                                                        " out of " + (runNumberPickerSets.getValue()) + ". Click OK to begin next set.");
                                                if (settingsRunDrillAudioCheckBox.isChecked()) {
                                                    drillCompleteSound.setLooping(true);
                                                    drillCompleteSound.start();
                                                    Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                                                    // Vibrate for 500 milliseconds
                                                    v.vibrate(500);
                                                }
                                                alertDialog.setButton(Dialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        inBuffer = true;
                                                        runsDrillTimer.start();

                                                    }

                                                });
                                                alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                                                    @Override
                                                    public void onDismiss(DialogInterface dialog) {
                                                        drillCompleteSound.pause();
                                                        //alertDialogTimer.cancel();
                                                        //if the wakeLock is currently being used, we now release it
//                                                        if (wakeLock.isHeld()) {
//                                                            wakeLock.release();
//                                                        }
                                                    }
                                                });
                                                alertDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                                                    @Override
                                                    public void onCancel(DialogInterface dialog) {
                                                        drillCompleteSound.pause();
                                                        alertDialogTimer.cancel();
                                                        //if the wakeLock is currently being used, we now release it
                                                        if (wakeLock.isHeld()) {
                                                            wakeLock.release();
                                                        }
                                                    }
                                                });
                                                alertDialog.show();

                                            }//THIS IS THE END OF THE STUFF THAT HAPPENS IF WE HAVE JUST FINISHED A SET, BUT NOT THE ENTIRE DRILL
                                        }
                                    }.start();

                                }
                                //Toast.makeText(MainActivity.this, "Drill is selected", Toast.LENGTH_LONG).show();


                            }
                            //Toast.makeText(MainActivity.this, "Endurance is selected", Toast.LENGTH_LONG).show();
                        }

                    }


                    if (runRadioButtonEndurance.isChecked()) {

                        //we are just now starting a new endurance run, so we reset the time to 0
                        runsEnduranceTimerCurrentTime = 0;
                        inBuffer = true;
                        currentBufferSeconds = 0;

                        //if the setting to play an audio when a 'personal best' is broken is checked,
                        //      then we will need to know what the 'personal bests' are for this pattern
                        if (settingsRunEnduranceAudioBrakePB.isChecked()) {
                            personalBestIntentionalEnd = getPersonalBest("intentionalEnd");
                            personalBestUnintentionalEnd = getPersonalBest("unintentionalEnd");
                            personalBestIntentionalEndAudioHasPlayed = false;
                            personalBestUnintentionalEndAudioHasPlayed = false;
                        }

                        //now we show the dialog that shows the timer and has the buttons
                        //  to end the timer due either because the juggling has ended intentionally
                        //  or unintentionally
                        final AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
                        // alertDialog.setIcon(android.R.drawable.ic_dialog_alert);
                        alertDialog.setTitle("Endurance");
                        alertDialog.setMessage("Endurance will begin in: " + formattedTime(totalBufferSeconds - currentBufferSeconds));
                        alertDialog.setButton(Dialog.BUTTON_POSITIVE, "intentional ending  :-)", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                        //this is what happens if the user clicks 'end intentionally'
                                runsEnduranceTimer.cancel();
                                currentlyInEndurRun = false;
                                //if this is checked then it means that the user wants to use the feature where
                                //      the headphone button is used to indicate that a run has ended, this will
                                //      record that run and immediately start the buffer for another run
                                if (runHeadphoneCheckBox.isChecked()) {
                                    //unlock touches
                                    //getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                                }

                                //if the wakeLock is currently being used, we now release it
                                if (wakeLock.isHeld()) {
                                    wakeLock.release();
                                }

                                if (runsEnduranceTimerCurrentTime == 0) {
                                    //debugging
                                    //Toast.makeText(MainActivity.this,"Endurance attempt canceled.", Toast.LENGTH_LONG).show();
                                } else {
                                    showReasonForIntentionalEndDialog(false);
                                }

                            }

                        });
                        alertDialog.setButton(Dialog.BUTTON_NEGATIVE, "unintentional ending  :-(", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                runsEnduranceTimer.cancel();
                                currentlyInEndurRun = false;
                                //if this is checked then it means that the user wants to use the feature where
                                //      the headphone button is used to indicate that a run has ended, this will
                                //      record that run and immediately start the buffer for another run
                                if (runHeadphoneCheckBox.isChecked()) {
                                    //unlock touches
                                    //getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                                }
                                //if the wakeLock is currently being used, we now release it
                                if (wakeLock.isHeld()) {
                                    wakeLock.release();
                                }
                                //runsEnduranceTimerCurrentTime = 0;

                                if (runsEnduranceTimerCurrentTime == 0) {
                                    //debugging
                                    //Toast.makeText(MainActivity.this,"Endurance attempt canceled.", Toast.LENGTH_LONG).show();
                                } else {
                                    showLastThoughtBeforeUnintentionalEndDialog(false);
                                }


                            }

                        });
                        alertDialog.setButton(Dialog.BUTTON_NEUTRAL, "Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //debugging
                                //Toast.makeText(MainActivity.this, "Endurance attempt canceled.", Toast.LENGTH_LONG).show();

                                alertDialog.cancel();
                                runsEnduranceTimer.cancel();
                                currentlyInEndurRun = false;
                                if (runHeadphoneCheckBox.isChecked()) {
                                    //unlock touches
                                    //getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                                }
                                //if the wakeLock is currently being used, we now release it
                                if (wakeLock.isHeld()) {
                                    wakeLock.release();
                                }
                                //runsEnduranceTimerCurrentTime = 0;
                            }

                        });
                        alertDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                            @Override
                            public void onCancel(DialogInterface dialog) {
                                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                                //debugging
                                Toast.makeText(MainActivity.this, "Endurance attempt canceled.", Toast.LENGTH_LONG).show();

                                alertDialog.cancel();
                                runsEnduranceTimer.cancel();
                                currentlyInEndurRun = false;
                                if (runHeadphoneCheckBox.isChecked()) {
                                    //unlock touches
                                    //getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                                }
                                //if the wakeLock is currently being used, we now release it
                                if (wakeLock.isHeld()) {
                                    wakeLock.release();
                                }
                            }
                        });

                        alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                            @Override
                            //if we leave this dialog then we are done with the drill,
                            //      so we can allow the screen to be able to time out again if
                            //      it currently set not to
                            public void onDismiss(DialogInterface dialog) {
                                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

                                //debugging
                                //Toast.makeText(MainActivity.this, "Endurance attempt canceled.", Toast.LENGTH_LONG).show();

                                alertDialog.cancel();
                                runsEnduranceTimer.cancel();
                                currentlyInEndurRun = false;
                                if (runHeadphoneCheckBox.isChecked()) {
                                    //unlock touches
                                    //+getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                                }
                                //if the wakeLock is currently being used, we now release it
                                if (wakeLock.isHeld()) {
                                    wakeLock.release();
                                }
                            }
                        });

                        alertDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
                            @Override
                            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {


                                //todo operation headphone button:---------------
                                //  -make the cancel stuff
                                //  -make the drill stuff

                                //next:

                                //  -a triple click should cancel everything
                                //  -drill; 1 = drop happened, start next set(after end noise happens)
                                //

                                //ALL THE GATHERINGS OF INFO IN THIS INCASE I FEEL LIKE WORKING ON IT EVER :)
                                //-this note was at 'drillHeadset1'
                                //this is stuff to do with the drill headset button, NOT CURRENTLY WORKING
                                //so my thinking here is that if the headset button is clicked, then we should end the current
                                //      set and (either after another click or automatically), begin the next set, I thought I copied over everything
                                //      that should do this, but it does not work as imagined. It ends up telling us that we completed set 4/3

                                //-this note was at 'drillHeadset2'
                                //this is the drill headset button stuff, it needs some work, i do not know what needs to be done, just go through and follow all the logic and see
                                //      where it falls apart

                                //-this note was at 'drillHeadset3'
                                // add the explanation of the buttons for drill runs


////lock touches
//    getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
//                         WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
//    getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
//
////unlock touches
//    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
//    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);



                                if (runHeadphoneCheckBox.isChecked()) {
                                    if (keyCode == KeyEvent.KEYCODE_HEADSETHOOK) {

                                        headphoneClickCounter++;

                                        Handler handler = new Handler();
                                        Runnable r = new Runnable() {

                                            @Override
                                            public void run() {
                                                if (headphoneClickCounter == 2) {
                                                    if (currentlyInEndurRun) {
                                                        //Log.d("headset", "single click!");
                                                        runsEnduranceTimer.cancel();
                                                        currentlyInEndurRun = false;

                                                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

                                                        //if the wakeLock is currently being used, we now release it
                                                        if (wakeLock.isHeld()) {
                                                            wakeLock.release();
                                                        }

                                                        showLastThoughtBeforeUnintentionalEndDialog(true);


                                                        alertDialog.cancel();

                                                        //now we wait 1 second before simulating a press on the begin ubtton to start
                                                        //  another run
                                                        Runnable r = new Runnable() {
                                                            @Override
                                                            public void run() {
                                                                runTimerBeginButton.performClick(); //<-- put your code in here.
                                                            }
                                                        };

                                                        Handler h = new Handler();
                                                        h.postDelayed(r, 1000); // <-- the "1000" is the delay time in miliseconds.
                                                    }else if(currentlyInDrillSet){
                                                        //Log.d("headsetDrill", "single click!");
                                                        drillHeadphoneClicked = true;
                                                    }

                                                }
                                                // double click *********************************
                                                if (headphoneClickCounter == 4) {
                                                    //Log.d("headset", "dbl click!");
                                                    runsEnduranceTimer.cancel();
                                                    currentlyInEndurRun = false;

                                                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);


                                                    //if the wakeLock is currently being used, we now release it
                                                    if (wakeLock.isHeld()) {
                                                        wakeLock.release();
                                                    }

                                                    showReasonForIntentionalEndDialog(true);
                                                    alertDialog.cancel();
                                                    //now we wait 1 second before simulating a press on the begin ubtton to start
                                                    //  another run
                                                    Runnable r = new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            runTimerBeginButton.performClick(); //<-- put your code in here.
                                                        }
                                                    };

                                                    Handler h = new Handler();
                                                    h.postDelayed(r, 1000); // <-- the "1000" is the delay time in miliseconds.


                                                }
                                                if (headphoneClickCounter == 6) {
                                                    //Log.d("headset", "trpl click!");
                                                    alertDialog.cancel();
                                                    runsEnduranceTimer.cancel();
                                                    currentlyInEndurRun = false;

                                                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                                                    
                                                    //if the wakeLock is currently being used, we now release it
                                                    if (wakeLock.isHeld()) {
                                                        wakeLock.release();
                                                    }
                                                    //runsEnduranceTimerCurrentTime = 0;
                                                }
                                                if (headphoneClickCounter == 8) {
                                                    //Log.d("headset", "4 click!");

                                                }
                                                headphoneClickCounter = 0;
                                            }
                                        };
                                        if (headphoneClickCounter == 1) {
                                            handler.postDelayed(r, 1500);
                                        }
                                        ////Log.d("headset", "pushed");
                                        return true;

                                        //return dialog.onKeyDown(keyCode, event);
                                    }
                                }


                                return false;
                            }
                        });


                        alertDialog.show();
                        //----------------------------------------------------------------
                        //TODO OPERATION NEW TIMER
                        //  -FIRST FOCUS ON ENDURANCE, THEN DECIDE IF I AM GOING TO DO DRILL TOO,
                        //      WE COULD RUN SOME TESTS TO SEE IF DRILL IS WORKING AS IS
                        //  -when the begin button is clicked, the time should be recorded(take into account
                        //      the buffer time too), and then when the run ends, the the begin time can
                        //      be compared to end time and that is the number of seconds
                        //  -there is an issue of displaying the time as the run goes on, but that is
                        //      not really very important since the user is not looking at their phone,
                        //      they are juggling
                        //  -there is also the issue of playing the sounds to let the user know that they
                        //      just broke a record, if we just let the phone completely go to sleep
                        //      then i do not know if it can do that. I COULD TEMPORARILY DISABLE
                        //      THE SOUNDS.
                        //  STEPS TO ACTUALLY DO:
                        //      -find out how runsEnduranceTimerCurrentTime is getting set and change it
                        //          to be set by taking the difference between the current time and the time
                        //          that the timer began
                        //----------------------------------------------------------------

                        runsEnduranceTimer = new CountDownTimer(1000, 1000) {
                            @Override
                            public void onTick(long millisUntilFinished) {
                                //check to see if we are still in the buffer
                                if (inBuffer) {
                                    //if we are, then check to see if this is the end of the buffer
                                    if (currentBufferSeconds >= totalBufferSeconds) {
                                        inBuffer = false;
                                        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                                        // Vibrate for 500 milliseconds
                                        v.vibrate(500);
                                        currentlyInEndurRun = true;
                                        if (runHeadphoneCheckBox.isChecked()) {
                                            //Log.d("flags", "on");

                                            alertDialog.setCanceledOnTouchOutside(false);


                                            //lock touches and keep screen from timing out
//                                            getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
//                                                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                                        }

                                    } else {
                                        //if we are still supposed to be in the buffer, then update the message with
                                        //      the current buffer time
                                        alertDialog.setMessage("Endurance will begin in: " + formattedTime(totalBufferSeconds - currentBufferSeconds));
                                    }
                                } else {//if we are not in the buffer, then show the endurance timer
                                    alertDialog.setMessage(formattedTime(runsEnduranceTimerCurrentTime));

                                }
                            }

                            public void onFinish() {
                                ////Log.d("audio", "0");
                                //if we are in the buffer, then add on to the current buffer time
                                if (inBuffer) {
                                    currentBufferSeconds++;
                                } else {//but if we are not in the buffer, then add on to the endurance timer
                                    runsEnduranceTimerCurrentTime++;
                                    ////Log.d("audio", "1");
                                    if (settingsRunEnduranceAudioBrakePB.isChecked()) {
                                        if (!personalBestIntentionalEndAudioHasPlayed &&
                                                personalBestIntentionalEnd < runsEnduranceTimerCurrentTime) {
                                            //Log.d("intentionalEndAudio", "2");
                                            //if personalBestIntentionalEnd is not 0, then play audio for breaking personal best for intentionalEnd.
                                            //  we don't want to hear the sound if the 'personal best' we are breaking is 0 because we
                                            //  havn't set a personal best yet
                                            if (personalBestIntentionalEnd != 0) {
                                                //Log.d("intentionalEndAudio", "3");
                                                enduranceIntentionalEndPersonalBestBrokenSound.start();
                                                Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                                                // Vibrate for 500 milliseconds
                                                v.vibrate(500);
                                            }
                                            //and set this to true so that we know not to come back in here
                                            personalBestIntentionalEndAudioHasPlayed = true;
                                        }
                                        if (!personalBestUnintentionalEndAudioHasPlayed &&
                                                personalBestUnintentionalEnd < runsEnduranceTimerCurrentTime) {
                                            //Log.d("unintentionalEndAudio", "1");
                                            //if personalBestIntentionalEnd is not 0, then play audio for breaking personal best for unintentionalEnd.
                                            //  we don't want to hear the sound if the 'personal best' we are breaking is 0 because we
                                            //  havn't set a personal best yet
                                            if (personalBestUnintentionalEnd != 0) {
                                                //Log.d("unintentionalEndAudio", "2");
                                                enduranceUnintentionalEndPersonalBestBrokenSound.start();
                                                Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                                                // Vibrate for 500 milliseconds
                                                v.vibrate(500);
                                            }
                                            //and set this to true so that we know not to come back in here
                                            personalBestUnintentionalEndAudioHasPlayed = true;
                                        }

                                    }

                                }
                                runsEnduranceTimer.start();
                            }
                        }.start();
                    }
                }
            }
        });

        runRadioButtonDrill = (RadioButton) findViewById(R.id.runRadButDrill);
        runRadioButtonDrill.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showInfoDialog("Drill run", "An drill run is created by setting a number of sets that are to be completed as well as how long " +
                        "the sets should be. If 'Restart set on unintentionalEnds' is selected then a set must be completed without unintentionalEndping before " +
                        "continuing onto the next set. UnintentionalEnds are indicated by clicking the 'unintentionalEnd' button. The number of unintentionalEnds is recorded.");
                return true;
            }
        });
        runRadioButtonEndurance = (RadioButton) findViewById(R.id.runRadButEndurance);
        runRadioButtonEndurance.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showInfoDialog("Endurance run", "An endurance run will time you until you stop juggling. You will be prompted to select whether " +
                        "you stopped due to a intentionalEnd or a unintentionalEnd.");
                return true;
            }
        });

        runRadioGroup = (RadioGroup) findViewById(R.id.runTypeRadioGroup);
        runRadioGroup.check(runRadButEndurance);
        runRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton rb = (RadioButton) group.findViewById(checkedId);
                switch (rb.getId()) {
                    case R.id.runRadButEndurance://fixDrillVisibility
                        setVisiblityDrillSettings(View.GONE);
                        runHeadphoneCheckBox.setVisibility(View.VISIBLE);
                        break;
                    case R.id.runRadButDrill:
                        setVisiblityDrillSettings(View.VISIBLE);
                        runHeadphoneCheckBox.setVisibility(View.GONE);
                        break;

                }

            }
        });

        runsNumberOfSetsLabel = (TextView) findViewById(R.id.runsNumOfSetsLabel);
        runColonLabel2 = (TextView) findViewById(R.id.runColonLabel2);
        runColonLabel1 = (TextView) findViewById(R.id.runColonLabel1);

        runNumberPickerHours = (NumberPicker) findViewById(R.id.runDrillNumberPickerHours);
        runNumberPickerHours.setMinValue(0);
        runNumberPickerHours.setMaxValue(23);
        runNumberPickerHours.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);


        runNumberPickerMinutes = (NumberPicker) findViewById(R.id.runDrillNumberPickerMinutes);
        runNumberPickerMinutes.setMinValue(0);
        runNumberPickerMinutes.setMaxValue(59);
        runNumberPickerMinutes.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);

        runNumberPickerSeconds = (NumberPicker) findViewById(R.id.runDrillNumberPickerSeconds);
        runNumberPickerSeconds.setMinValue(0);
        runNumberPickerSeconds.setMaxValue(59);
        runNumberPickerSeconds.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);

        runNumberPickerSets = (NumberPicker) findViewById(R.id.runDrillNumberPickerSets);
        runNumberPickerSets.setMinValue(0);
        runNumberPickerSets.setMaxValue(99);
        runNumberPickerSets.setValue(1);
        runNumberPickerSets.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);

        runDrillRestartSetCheckBox = (CheckBox) findViewById(R.id.runDrillRestartSetCheckBox);
        runDrillRestartSetCheckBox.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showInfoDialog("Restart set on drops", "If 'Restart set on drops' is selected then a set must be completed without unintentionalEndping before " +
                        "continuing onto the next set.");
                return true;
            }
        });
        runHeadphoneCheckBox = (CheckBox) findViewById(R.id.runHeadphoneCheckBox);
        runHeadphoneCheckBox.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {//'drillHeadset3'
                showInfoDialog("Use headphone button", "If this is checked, the headphone button can be used to end runs. In endurance, " +
                        "a single click indicates that the run ended unintentionally, a double click indicates that the run ended " +
                        "Intentionally. In this mode, immediately after a run is completed the buffer will begin followed by a new run. " +
                        " The screen must be on in order for the headphone button to work.");
                return true;
            }
        });

        runSessionDateAndTimeTextView = (TextView) findViewById(R.id.runSessionDateAndTimeTextView);
        runSessionDateAndTimeTextView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (runSessionDateAndTimeTextView.getText().toString().contains("most")) {//this is the info dialog we
                    //  show if the text currently reads "most recent session..." begin and end date/times
                    showInfoDialog("Most recent session", "These are the beginning and ending times of the last completed session. " +
                            "For more details on this session, and any other session, see the session section of the 'HIST' tab.");
                } else {//and this is the one we show if it currently reads 'current' begin date/time
                    showInfoDialog("Current session", "This is the begining time of the session you are currently in. Sessions begin" +
                            "automatically when you do a run. If more than 10 minutes pass without doing a run, the session will " +
                            "automatically end. Sessions can also be ended manually by using the 'end session' button, or completely" +
                            " closing the app. Minimizing the app will not end your session.");
                }
                return true;
            }
        });

        runSessionNoteButton = (Button) findViewById(R.id.runSessionNoteButton);
        runSessionNoteButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showInfoDialog("Session notes", "This button allows you to make any notes you want to associate with the current session." +
                        "  These notes can be viewed later in the sessions section of the 'HIST' tab by selecting a session and" +
                        " clicking the 'note' button.");
                return true;
            }
        });
        runSessionNoteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //this extracts the begin date/time from the textview next to the notes button in the run tab
                //      and passes it through so we know which note we are editing
                showSessionsNoteDialog(runSessionDateAndTimeTextView.getText().toString().split(" ")[2] + " " +
                        runSessionDateAndTimeTextView.getText().toString().split(" ")[3]);
            }
        });

        runSessionCloseButton = (Button) findViewById(R.id.runSessionCloseButton);
        runSessionCloseButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showInfoDialog("End session", "This button will end the current session. A session consists of all runs that occur " +
                        "between the sessions begin time and end time. You can view all the details of the runs in each session in the " +
                        " 'HIST' tab.");
                return true;
            }
        });
        runSessionCloseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAreYouSureDialog("ENDSESSION");

            }
        });
        runEndCurrentSessionIfOld();
        runFillSessionDateAndTimeTextView();


    }


    public void runFillSessionDateAndTimeTextView() {
        List<String> sessionBeginTimeColumn = getColumnFromDB("SESSIONS", "BEGINTIME");
        List<String> sessionEndTimeColumn = getColumnFromDB("SESSIONS", "ENDTIME");
        //if in a session,  then show the date/time that the session began,
        //          "current session-
        //              began: MM/dd/yy HH:mm:ss"
        if (runCurrentlyInSession()) {
            runSessionDateAndTimeTextView.setText("current session-\n" +
                    "began: " + sessionBeginTimeColumn.get(sessionBeginTimeColumn.size() - 1));
            runSessionNoteButton.setVisibility(View.VISIBLE);
            runSessionCloseButton.setVisibility(View.VISIBLE);
        } else {            //      if we are not, then show the begin and end date/times of the last session
            //          "most recent session-
            //              began: MM/dd/yy HH:mm:ss
            //              ended: MM/dd/yy HH:mm:ss"
            if (sessionBeginTimeColumn.size() > 0) {
                runSessionDateAndTimeTextView.setText("most recent session-\n" +
                        "began: " + sessionBeginTimeColumn.get(sessionBeginTimeColumn.size() - 1) +
                        "\nended: " + sessionEndTimeColumn.get(sessionEndTimeColumn.size() - 1));
                runSessionNoteButton.setVisibility(View.GONE);
                runSessionCloseButton.setVisibility(View.GONE);
            } else {
                runSessionDateAndTimeTextView.setText("No previous sessions.");
                runSessionNoteButton.setVisibility(View.GONE);
                runSessionCloseButton.setVisibility(View.GONE);
            }

        }

    }

    public void runEndCurrentSessionIfOld() {
        if (runCurrentlyInSession()) {
            //if currently in a session, check to see if the most recent run ended at least 10 minutes ago,
            //  if it did then we end it by using the time of the most recent run
            if (timeSince("minutes", dateAndTimeOfMostRecentRun()) > 9) {
                //   if it was, then end the last session by using the time of the most recent run
                String mostRecentID = getColumnFromDB("SESSIONS", "ID").get(getColumnFromDB("SESSIONS", "ID").size() - 1);
                //Log.d("Session", "ended due to > 9");
                myDb.updateData("SESSIONS", "ENDTIME", "ID", mostRecentID, dateAndTimeOfMostRecentRun());
                histPopulateSessionDateAndTimesSpinner();
                runFillSessionDateAndTimeTextView();
                fillHistoryRecordsTextView();

            }

        }
    }

    public void showSessionsNoteDialog(final String beginTime) {
//we get sent here either from the run tab or from the history tab, we pass the string that will be in the BEGINTIME column of the sessions
        //  note that we want. if we get passed a blank or something that is not a valid begintime, then we show a toast that tells the user
        //  that it is not valid

        //use the begintime that was passed through to get the current note
        String currentNote = getCellFromDB("SESSIONS", "NOTES", "BEGINTIME", beginTime);

        View view = (LayoutInflater.from(MainActivity.this)).inflate(R.layout.sessionnotedialog, null);

        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(MainActivity.this);
        alertBuilder.setView(view);

        final EditText sessionNoteDialogEditText = (EditText) view.findViewById(R.id.sessionNoteDialogEditText);
        //fill the edittext with the note that we got above
        sessionNoteDialogEditText.setText(currentNote);
        alertBuilder.setCancelable(true)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //save whatever is in the edittext as the note for this session in the db
                        updateDataInDB("SESSIONS", "NOTES", "BEGINTIME", beginTime, sessionNoteDialogEditText.getText().toString());
                    }
                });

        alertBuilder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {

            }
        });
        Dialog dialog = alertBuilder.create();

        dialog.show();
    }


    //  so far as the deciding when a session should be considered 'ended' we probably want to make the
    //      dateAndTimeOfMostRecentRun() be the time the run began + the length of the run, not just the begining of the run.
    //      However, for now we are calling a session ended at the time that the final run began.
    public String dateAndTimeOfMostRecentRun() {
        int secondsSinceMostRecentRun = 999999999;
        String dateAndTimeOfMostRecentRun = "01-01-01 01:01";
        //this cycles through every column of of history endurance table, it contains the information on every endurance run
        //  that has happened. We skip the 1st clumn because it is the names of the different combinations of modifiers.
        for (int i = 2; i < myDb.getFromDB("HISTORYENDURANCE").getColumnCount(); i++) {
            //since we are going through the table 1 column at a time, we want to get the column name of the column that we are
            //  currently dealing with
            String columnName = myDb.getFromDB("HISTORYENDURANCE").getColumnName(i);

            //now we use that column name to fill up this list with each cell of the column. 1 cell = 1 item in the list. inside of a cell
            //  is all the information for each modifier/pattern combination. the format for endurance entries is:
            // (end explanation)(intentionalEnd/unintentionalEnd)(run length)(date/time), as many entries as needed are hed in a single cell, the format just
            //  repeats over and over.
            List<String> listOfAllCellsInTheColumn = getColumnFromDB("HISTORYENDURANCE", columnName);


            //now we cycle through each of the list items(the cells in our column), each one of these represents a possible different unique
            //  combination of modifiers & pattern. If it contains anything, then we know it is indeed a unique combination, once we know
            //  that, we want to find out if it contains a run that ended in a intentionalEnd and/or unintentionalEnd
            for (int j = 0; j < listOfAllCellsInTheColumn.size(); j++) {
                //Toast.makeText(getBaseContext(), listOfAllCellsInDB.get(j), Toast.LENGTH_LONG).show();


                //here we check to make sure that the list item(cell) is not null or empty, it has to have something in it or we don't
                //  want anything to do with it
                if (listOfAllCellsInTheColumn.get(j) != null && !listOfAllCellsInTheColumn.get(j).isEmpty()) {

                    //now we split the item(cell) on the parenthesis and put all the split parts into a list
                    String[] splitListOfAllCellsInDB = listOfAllCellsInTheColumn.get(j).split("\\)\\(");


                    //Here we are setting off to go gather all the endurance runs that ended intentionally and
                    //      we are counting them, and seeing how many days it has been since them, and how long we
                    //      spent in them

                    //we go through each item in the list
                    for (int k = 0; k < splitListOfAllCellsInDB.length; k++) {

                        String endType = splitListOfAllCellsInDB[k];

                        //it is set up so that all the stats that are for 'unintentionalEnd' are in 'arrayToReturn' indexes that are 100 higher than
                        //  their 'intentionalEnd' equivalents, so here we check to see which we are dealing with, and set our 'indexAdder'
                        if (endType.equals("intentionalEnd") || endType.equals("unintentionalEnd")) {

                            //this is getting the time that the run actually ended, it is its begin time plus its length
                            String endDateAndTimeOfThisEndur = addTimeToStringDate("SECOND",
                                    Integer.parseInt(splitListOfAllCellsInDB[k + 1]), splitListOfAllCellsInDB[k + 2]);


                            //this is getting how many seconds since doing an endurance run that ended in a intentionalEnd. If we find a run
                            //  that is more recent then our currently most recent intentionalEnd run, then we update the most recent intentionalEnd
                            //  run to whatever the more recent one was.
                            if (secondsSinceMostRecentRun > timeSince("seconds", endDateAndTimeOfThisEndur)) {
                                secondsSinceMostRecentRun = timeSince("seconds", endDateAndTimeOfThisEndur);
                                dateAndTimeOfMostRecentRun = endDateAndTimeOfThisEndur;
                                //Log.d("dateAndTimeOfMost..", endDateAndTimeOfThisEndur);
                            }
                        }
                    }
                }
            }
        }//at this point 'minutesSinceMostRecentRun' is set as the end time of the most recent endurance run, now we check to see if there
        //      is a more recent drill run
        for (int i = 2; i < myDb.getFromDB("HISTORYDRILL").getColumnCount(); i++) {
            String columnName = myDb.getFromDB("HISTORYDRILL").getColumnName(i);
            List<String> listOfAllCellsInDB = getColumnFromDB("HISTORYDRILL", columnName);


            for (int j = 0; j < listOfAllCellsInDB.size(); j++) {
                //Toast.makeText(getBaseContext(), listOfAllCellsInDB.get(j), Toast.LENGTH_LONG).show();
                //Log.d("listOfAl...get(j)", listOfAllCellsInDB.get(j));
                //here we check to make sure that a line has at least 1 number or 1 letter before even trying to
                //      add it as a siteswap, it prevents crashes
                if (listOfAllCellsInDB.get(j) != null && !listOfAllCellsInDB.get(j).isEmpty()) {

                    //now we split listOfAllCellsInDB.get(j) on the parenthesis and put all the split parts into a list

                    String[] splitListOfAllCellsInDB = listOfAllCellsInDB.get(j).split("\\)\\(");
                    //we go through each item in the list

                    for (int k = 0; k < splitListOfAllCellsInDB.length; k++) {
                        Log.d("splitLisA..", splitListOfAllCellsInDB[k]);

                        if (k % 4 == 0) {
                            splitListOfAllCellsInDB[k] = splitListOfAllCellsInDB[k].replace("(", "").replace(")","");

//this length is the number of sets time the length of the sets plus the time spent in failed sets
                            Log.d("splitLis..", splitListOfAllCellsInDB[k]);
                            int drillLength = 0;
                            if (splitListOfAllCellsInDB[k].split(",").length > 1) {
                                //todo(this seems to be working fine, but ill leave it just in case for now)
                                //      THE ABOVE LINE MUST BE COMMENTED OUT
                                //          IF WE HAVE ANY DRILL SESSIONS IN OUR HISTORY,
                                //          I DO NOT KNOW WHY IT BREAKS IF WE COMMENT IT OUT THOUGH
                                drillLength = Integer.parseInt(splitListOfAllCellsInDB[k + 1]) *
                                        Integer.parseInt(splitListOfAllCellsInDB[k + 2]) +
                                        Integer.parseInt(splitListOfAllCellsInDB[k].split(",")[1]);
                            }else {
                                drillLength = Integer.parseInt(splitListOfAllCellsInDB[0]);
                            }

//this is getting the time that the run actually ended, it is its begin time plus its length
                                String endDateAndTimeOfThisDrill = addTimeToStringDate("SECOND", drillLength, splitListOfAllCellsInDB[k + 3]);

                                Log.d("k..", Integer.toString(k));
                                Log.d("secondsSince..", Integer.toString(secondsSinceMostRecentRun));
                                Log.d("timeSince..", Integer.toString(timeSince("seconds", endDateAndTimeOfThisDrill)));
                                //this keeps track of the most recent drill run
                                if (secondsSinceMostRecentRun > timeSince("seconds", endDateAndTimeOfThisDrill)) {
                                    secondsSinceMostRecentRun = timeSince("seconds", endDateAndTimeOfThisDrill);
                                    Log.d("dateAndTimeOfMost..", endDateAndTimeOfThisDrill);
                                    dateAndTimeOfMostRecentRun = endDateAndTimeOfThisDrill;

                            }
                        }
                    }
                }
            }
        }

        return dateAndTimeOfMostRecentRun.replace(")", "");

    }

    //the only way to get here is if we are adding a run to the history, which means we completed a run.
    //  this function is going to start a session if we are not in one, or it is going to end a session if we are in one, but have done no runs
    //      in at least 10 minutes, then it will start a new session.
    public void runStartSessionIfNeeded() {


        //when we start a run, we first want to check and see if we are in a session,
        //  if we are currently in a session:
        if (runCurrentlyInSession()) {
            //Log.d("currentlyInSession", "true");
            //if currently in a session, check to see if the most recent run ended at least 10 minutes ago
            if (timeSince("minutes", dateAndTimeOfMostRecentRun()) > 9) {
                //   if it was, then end the last session by using the time of the most recent run
                String mostRecentID = getColumnFromDB("SESSIONS", "ID").get(getColumnFromDB("SESSIONS", "ID").size() - 1);
                //Log.d("Session", "ended due to > 9");
                myDb.updateData("SESSIONS", "ENDTIME", "ID", mostRecentID, dateAndTimeOfMostRecentRun());
                histPopulateSessionDateAndTimesSpinner();
                runFillSessionDateAndTimeTextView();
                fillHistoryRecordsTextView();
                //and begin a new session
                addDataToDB("SESSIONS", "BEGINTIME", runCurrentDateAndTimeOfMostRecentRunBegin);
                //update the text that shows us the date/time of either the current or the most recent session
                runFillSessionDateAndTimeTextView();
            }//if it was not, then we stay in the same session, and do nothing now.

        } else {//and if we are not currently in a session, start a session
            //to start a session, we put the current date/time in the next 'BEGINTIME' cell
            addDataToDB("SESSIONS", "BEGINTIME", runCurrentDateAndTimeOfMostRecentRunBegin);
            //Log.d("Session", "begin due to !runCurrentlyInSession()");
            //update the text that shows us the date/time of either the current or the most recent session
            runFillSessionDateAndTimeTextView();

        }
    }

    public boolean runCurrentlyInSession() {
        boolean currentlyInSession = true;
        List<String> sessionEndTimeColumn = getColumnFromDB("SESSIONS", "ENDTIME");

        //if the size is 0, then we know that we have never started a session before, and thus are not currently in one
        //  or if the most recent endTime cell is not empty, then we know that out most recent session has been
        //  completed, and thus we are not currently in a session
        if (sessionEndTimeColumn.size() == 0) {
            currentlyInSession = false;
            //Log.d("currentlyInSession", "false because (sessionEndTimeColumn.size() == 0)");
        } else if (sessionEndTimeColumn.get(sessionEndTimeColumn.size() - 1) == null) {
            currentlyInSession = true;
            //Log.d("currentlyInSession", "true because (sessionEndTimeColumn.get(sessionEndTimeColumn.size()-1) == null)");

        } else if (!sessionEndTimeColumn.get(sessionEndTimeColumn.size() - 1).isEmpty()) {
            currentlyInSession = false;
            //Log.d("currentlyInSession", "false because (!sessionEndTimeColumn.get(sessionEndTimeColumn.size()-1).isEmpty())");

        }
        return currentlyInSession;
    }

    public void prepareAddTabActivity() {

        TextView addPatternHeader = (TextView) findViewById(R.id.addPatternHeader);
        addPatternHeader.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showInfoDialog("Pattern", "In this section, you can view and edit the details of the currently selected pattern in the 'Main" +
                        " Input' at the top of the screen.");
                return false;
            }
        });

        addDeletePatternButton = (Button) findViewById(R.id.addPatDeleteBut);
        addDeletePatternButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showInfoDialog("Delete pattern", "This will completely delete the currently selected pattern from your database.");
                return true;
            }
        });
        addDeletePatternButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!patternsATV.getText().toString().equals("")) {

                    new AlertDialog.Builder(MainActivity.this)
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setTitle("Deleting Pattern")
                            .setMessage("Are you sure you want to permanently delete pattern '" + currentEntryNameToShow() + "' ?")
                            .setPositiveButton("Yessir", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    //make a toast explaining what just happened
                                    Toast.makeText(MainActivity.this, "Pattern '" + currentEntryNameToShow() + "' deleted", Toast.LENGTH_LONG).show();

                                    //remove all history items for that pattern
                                    for (int i = 0; i < getColumnFromDB("HISTORYENDURANCE", currentEntryName()).size(); i++) {
                                        myDb.updateData("HISTORYENDURANCE", currentEntryName(), "ID", Integer.toString(i + 1), "");
                                        myDb.updateData("HISTORYDRILL", currentEntryName(), "ID", Integer.toString(i + 1), "");
                                    }

                                    //delete the pattern from the pattern list
                                    myDb.deleteData("PATTERNS", "ENTRYNAME", currentEntryName());


                                    patternsATV.setText("");
                                    //Log.d("z", "6");
                                    fillPatternMainInputsFromDB();

                                    clearAddPatternInputs();


                                }

                            })
                            .setNegativeButton("No", null)
                            .show();


                } else {
                    Toast.makeText(MainActivity.this, "Select a pattern in the main input to delete", Toast.LENGTH_LONG).show();

                }
            }
        });

        addAddPatternButton = (Button) findViewById(R.id.addPatNewBut);
        addAddPatternButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showInfoDialog("Add pattern", "This will open a box to add a new pattern to your database.");
                return true;
            }
        });
        addAddPatternButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //showPatterns(); //just used temporarily for debugging, shows the patterns in a message which have input to the db
                //Toast.makeText(MainActivity.this, "Run", Toast.LENGTH_LONG).show();
                View view = (LayoutInflater.from(MainActivity.this)).inflate(R.layout.addpatterndialog, null);

                AlertDialog.Builder alertBuilder = new AlertDialog.Builder(MainActivity.this);

                alertBuilder.setView(view);

                TextView newAddPatInputDialogBoxNameTV = (TextView) view.findViewById(R.id.newAddPatInputDialogBoxNameTV);
                newAddPatInputDialogBoxNameTV.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        showInfoDialog("Pattern name", "This is where you input the name of the pattern you are adding.");
                        return true;
                    }
                });


                final EditText newAddPatInputDialogBoxName = (EditText) view.findViewById(R.id.newAddPatInputDialogBoxName);


                TextView newAddPatInputDialogNumTV = (TextView) view.findViewById(R.id.newAddPatInputDialogBoxNumTV);
                newAddPatInputDialogNumTV.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        showInfoDialog("Object number", "This is where you input the number of objects used in the pattern you are adding.");
                        return true;
                    }
                });

                final Spinner newAddPatInputDialogNum = (Spinner) view.findViewById(R.id.newAddPatInputDialogBoxNum);
                ArrayAdapter<String> numOfObjsSpinnerAdapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.select_dialog_item, addPatNumberOfObjectsForSpinner);
                numOfObjsSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                newAddPatInputDialogNum.setAdapter(numOfObjsSpinnerAdapter);
                newAddPatInputDialogNum.setSelection(numOfObjsSpinner.getSelectedItemPosition());
                newAddPatInputDialogNum.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        showInfoDialog("Object number", "This is where you input the number of objects used in the pattern you are adding.");
                        return false;
                    }
                });
                newAddPatInputDialogNum.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        // Toast.makeText(getBaseContext(), parent.getItemAtPosition(position) + " selected", Toast.LENGTH_LONG).show();

                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                TextView newAddPatInputDialogBoxTypeTV = (TextView) view.findViewById(R.id.newAddPatInputDialogBoxTypeTV);
                newAddPatInputDialogBoxTypeTV.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        showInfoDialog("Prop type", "This is where you input the type of prop used in the pattern you are adding.");
                        return true;
                    }
                });

                final MultiAutoCompleteTextView newAddPatInputDialogBoxType =
                        (MultiAutoCompleteTextView) view.findViewById(R.id.newAddPatInputDialogBoxType);
                ArrayAdapter<String> objsTypeMATVAdapter =
                        new ArrayAdapter<>(MainActivity.this, android.R.layout.select_dialog_item, addPatObjTypeForMATV);
                //objsTypeMATVAdapter.notifyDataSetChanged();
                newAddPatInputDialogBoxType.setAdapter(objsTypeMATVAdapter);

                newAddPatInputDialogBoxType.setThreshold(0);//this is number of letters that must match for autocomplete to suggest a word
                newAddPatInputDialogBoxType.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());
                newAddPatInputDialogBoxType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        // Toast.makeText(getBaseContext(), parent.getItemAtPosition(position) + " selected", Toast.LENGTH_LONG).show();

                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
                newAddPatInputDialogBoxType.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
                        if (hasFocus) {
                            newAddPatInputDialogBoxType.showDropDown();
                            hideKeyboard();
                        } else {
                            updateMATV(newAddPatInputDialogBoxType, getCellFromDB("SETTINGS", "MISC", "ID", "2"));
                        }
                    }
                });
                newAddPatInputDialogBoxType.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        newAddPatInputDialogBoxType.showDropDown();

                    }
                });
                newAddPatInputDialogBoxType.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                        if (newAddPatInputDialogBoxType.getText().toString().contains("Select all")) {
                            newAddPatInputDialogBoxType.setText("");
                            //NEW PHONE: the -1 was added because without it the new phone was crashing there
                            //      because the index was the same as the length, I dont know why this didnt crash the old phone too
                            for (int i = 0; i < newAddPatInputDialogBoxType.getAdapter().getCount()-1; i++) {
                                //showInfoDialog("ya");
                                //newAddPatInputDialogBoxType.clearListSelection();

                                newAddPatInputDialogBoxType.append(getCellFromDB("SETTINGS", "MISC", "ID", "2").split(",")[i] + ", ");
                            }

                        }
                        updateMATV(newAddPatInputDialogBoxType, getCellFromDB("SETTINGS", "MISC", "ID", "2"));
                    }
                });


                TextView newAddPatInputDialogBoxDescriptionTV = (TextView) view.findViewById(R.id.newAddPatInputDialogBoxDescriptionTV);
                newAddPatInputDialogBoxDescriptionTV.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        showInfoDialog("Pattern description", "This is where you input the description of the pattern you are adding.");
                        return true;
                    }
                });


                final EditText newAddPatInputDialogBoxDescription = (EditText) view.findViewById(R.id.newAddPatInputDialogBoxDescription);

                TextView newAddPatInputDialogBoxSiteswapTV = (TextView) view.findViewById(R.id.newAddPatInputDialogBoxSiteswapTV);
                newAddPatInputDialogBoxSiteswapTV.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        showInfoDialog("Siteswap", "This is where you input the siteswap of the pattern you are adding.");
                        return true;
                    }
                });


                final EditText newAddPatInputDialogBoxSiteswap = (EditText) view.findViewById(R.id.newAddPatInputDialogBoxSiteswap);


                TextView newAddPatInputDialogBoxLinkTV = (TextView) view.findViewById(R.id.newAddPatInputDialogBoxLinkTV);
                newAddPatInputDialogBoxLinkTV.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        showInfoDialog("Pattern link", "This is where you can input a link relevant to the pattern you are adding.");
                        return true;
                    }
                });


                final EditText newAddPatInputDialogBoxLink = (EditText) view.findViewById(R.id.newAddPatInputDialogBoxLink);


                alertBuilder.setCancelable(true)
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        });
                final Dialog dialog = alertBuilder.create();
                dialog.setOnShowListener(new DialogInterface.OnShowListener() {

                    @Override
                    public void onShow(final DialogInterface dialog) {

                        Button button = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                        button.setOnClickListener(new View.OnClickListener() {

                            @Override
                            public void onClick(View view) {


                                updateMATV(newAddPatInputDialogBoxType, getCellFromDB("SETTINGS", "MISC", "ID", "2"));


                                if (!newAddPatInputDialogBoxName.getText().toString().equals("") &&
                                        !newAddPatInputDialogBoxType.getText().toString().equals("")) {
                                    //since the ok button was just clicked, we want to add whatever the user has input


                                    String siteswapObjType = newAddPatInputDialogBoxType.getEditableText().toString();

                                    //we split the modifierMATV string into an array
                                    List<String> listOfSplitObjTypeString = new ArrayList<>();
                                    Collections.addAll(listOfSplitObjTypeString, siteswapObjType.split(", "));

                                    //now we alphabetize the list
                                    Collections.sort(listOfSplitObjTypeString, String.CASE_INSENSITIVE_ORDER);

                                    for (int j = 0; j < listOfSplitObjTypeString.size(); j++) {


                                        //we make the entry name by following this format @@##&&name@@##&&number@@##&&type@@##&&
                                        String entryName = buffer + newAddPatInputDialogBoxName.getText().toString() +
                                                buffer + newAddPatInputDialogNum.getSelectedItem().toString() +
                                                buffer + listOfSplitObjTypeString.get(j) + buffer;

                                        if (isValidInput(newAddPatInputDialogBoxName.getText().toString())) {

                                            if (containsCaseInsensitive(entryName, getColumnFromDB("PATTERNS", "ENTRYNAME"))) {
                                                Toast.makeText(MainActivity.this, "'" + newAddPatInputDialogBoxName.getText().toString() +
                                                        "' already exists for " + listOfSplitObjTypeString.get(j) +
                                                        ".", Toast.LENGTH_LONG).show();

                                            } else {


//                                        //but if it isn't empty or already in our database, we add it to the patterns list in the DB

//                                        //now we add our new pattern to the DB in the 'PATTERNS' table
                                                addDataToDB("PATTERNS", "ENTRYNAME", entryName);

                                                //and add description, siteswap, and link here.
                                                //     this is done the same way as when we change stuff in the add tab inputs
                                                //now we go through and update the DB from all the different inputs

                                                myDb.updateData("PATTERNS", "DESCRIPTION", "ENTRYNAME", entryName,
                                                        newAddPatInputDialogBoxDescription.getText().toString());

                                                myDb.updateData("PATTERNS", "SS", "ENTRYNAME", entryName,
                                                        newAddPatInputDialogBoxSiteswap.getText().toString());

                                                myDb.updateData("PATTERNS", "LINK", "ENTRYNAME", entryName,
                                                        newAddPatInputDialogBoxLink.getText().toString());


//                                                  //and add it to the main Patterns Input
                                                patternsATV.setText(newAddPatInputDialogBoxName.getText().toString());
                                                setSpinnerSelection(numOfObjsSpinner, newAddPatInputDialogNum.getSelectedItem().toString());
                                                setSpinnerSelection(objTypeSpinner, listOfSplitObjTypeString.get(j));

                                                //since we just added a new pattern to the DB, we want to update the mainPatternInputs with it
                                                //Log.d("z", "7");
                                                fillPatternMainInputsFromDB();

//                                        //debugging
                                                //Toast.makeText(MainActivity.this, "Pattern '"+entryName+"' added", Toast.LENGTH_LONG).show();
//
                                                clearAddPatternInputs();
                                            }
                                        }
                                    }
                                    //THIS IS JUST FOR DEBUGGING
                                    //Toast.makeText(MainActivity.this, newAddModifierInputDialog.getText().toString(), Toast.LENGTH_LONG).show();


                                    dialog.dismiss();
                                } else {

                                    Toast.makeText(MainActivity.this, "Name and/or prop type is empty.", Toast.LENGTH_LONG).show();

                                }
                            }
                        });
                    }
                });
                dialog.show();

                //this sets the width of the dialog to the width of the screen, and the height to half the height of the screen
                dialog.getWindow().setLayout(Resources.getSystem().getDisplayMetrics().widthPixels,
                        Resources.getSystem().getDisplayMetrics().heightPixels / 2);


            }
        });

        addBulkSiteswapButton = (Button) findViewById(R.id.addBulkSiteswapBut);
        addBulkSiteswapButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showInfoDialog("Bulk siteswap", "This will open a box to add an entire list of siteswaps to your database. More information is available" +
                        " inside the box.");
                return true;
            }
        });
        addBulkSiteswapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                updateDBfromAddInputs();

                View view = (LayoutInflater.from(MainActivity.this)).inflate(R.layout.addbulksiteswapdialog, null);

                AlertDialog.Builder alertBuilder = new AlertDialog.Builder(MainActivity.this);
                alertBuilder.setView(view);

                TextView newAddBulkSiteswapInputDialogBoxNameTV = (TextView) view.findViewById(R.id.newAddBulkSiteswapInputDialogBoxNameTV);
                newAddBulkSiteswapInputDialogBoxNameTV.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        showInfoDialog("Siteswap list", "This is where you input the list of siteswaps to be added. There must only be 1 siteswap per line. " +
                                " Valid siteswaps will automatically be added to your database based on their number of objects. " +
                                "\n\nExample:\n" +
                                "[33]\n" +
                                "(4,6x)*\n" +
                                "7531");
                        return false;
                    }
                });

                final EditText newAddBulkSiteswapInputDialogName = (EditText) view.findViewById(R.id.newAddBulkSiteswapInputDialogBoxName);


                TextView newAddBulkSiteswapInputDialogBoxTypeTV = (TextView) view.findViewById(R.id.newAddBulkSiteswapInputDialogBoxTypeTV);
                newAddBulkSiteswapInputDialogBoxTypeTV.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        showInfoDialog("Prop type", "This is where you pick which type of props you want the new siteswaps to be added to.");
                        return false;
                    }
                });

                final MultiAutoCompleteTextView newAddBulkSiteswapInputDialogBoxType =
                        (MultiAutoCompleteTextView) view.findViewById(R.id.newAddBulkSiteswapInputDialogBoxType);
                ArrayAdapter<String> objsTypeMATVAdapter =
                        new ArrayAdapter<>(MainActivity.this, android.R.layout.select_dialog_item, addPatObjTypeForMATV);
                //objsTypeMATVAdapter.notifyDataSetChanged();
                newAddBulkSiteswapInputDialogBoxType.setAdapter(objsTypeMATVAdapter);

                newAddBulkSiteswapInputDialogBoxType.setThreshold(0);//this is number of letters that must match for autocomplete to suggest a word
                newAddBulkSiteswapInputDialogBoxType.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());
                newAddBulkSiteswapInputDialogBoxType.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        showInfoDialog("Prop type", "This is where you pick which type of props you want the new siteswaps to be added to.");
                        return false;
                    }
                });
                newAddBulkSiteswapInputDialogBoxType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        // Toast.makeText(getBaseContext(), parent.getItemAtPosition(position) + " selected", Toast.LENGTH_LONG).show();
                        //Log.d("newAddBulkSit..", "6");
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
                newAddBulkSiteswapInputDialogBoxType.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
                        if (hasFocus) {
                            //Log.d("newAddBulkSit..", "5");
                            newAddBulkSiteswapInputDialogBoxType.showDropDown();
                            hideKeyboard();
                            //Log.d("newAddBulkSit..", "5.5");
                        }
                    }
                });
                newAddBulkSiteswapInputDialogBoxType.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //Log.d("newAddBulkSit..", "4");
                        newAddBulkSiteswapInputDialogBoxType.showDropDown();

                    }
                });
                newAddBulkSiteswapInputDialogBoxType.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                        //Log.d("newAddBulkSit..", "1");

                        if (newAddBulkSiteswapInputDialogBoxType.getText().toString().contains("Select all")) {
                            //Log.d("newAddBulkSit..", "2");
                            newAddBulkSiteswapInputDialogBoxType.setText("");
                            for (int i = 0; i < newAddBulkSiteswapInputDialogBoxType.getAdapter().getCount()-1; i++) {
                                //showInfoDialog("ya");
                                //Log.d("newAddBulkSit..", "3");
                                //newAddPatInputDialogBoxType.clearListSelection();
                                newAddBulkSiteswapInputDialogBoxType.append(getCellFromDB("SETTINGS", "MISC", "ID", "2").split(",")[i] + ", ");
                            }

                        }
                        updateMATV(newAddBulkSiteswapInputDialogBoxType, getCellFromDB("SETTINGS", "MISC", "ID", "2"));
                    }
                });


                alertBuilder.setCancelable(true)
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }


                        });
                Dialog dialog = alertBuilder.create();
                dialog.setOnShowListener(new DialogInterface.OnShowListener() {

                    @Override
                    public void onShow(final DialogInterface dialog) {

                        Button button = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                        button.setOnClickListener(new View.OnClickListener() {

                            @Override
                            public void onClick(View view) {

                                //we only want to do this if the siteswap list is not blank and the object type is not blank
                                if (!newAddBulkSiteswapInputDialogName.getText().toString().equals("") &&
                                        !newAddBulkSiteswapInputDialogBoxType.getText().toString().equals("")) {


                                    //use the objectType String to make an objectType list so that we can go through each individually
                                    String siteswapObjType = newAddBulkSiteswapInputDialogBoxType.getEditableText().toString();

                                    List<String> listOfSplitObjTypeString = new ArrayList<>();
                                    Collections.addAll(listOfSplitObjTypeString, siteswapObjType.split(", "));

                                    int numberOfSiteswapsAdded = 0;
                                    int numberOfSiteswapsFailed = 0;

                                    String[] originalInputArray =
                                            newAddBulkSiteswapInputDialogName.getText().toString().split("[\\r\\n]+");

                                    if (originalInputArray.length == 1 && originalInputArray[0].replace(" ", "").equals("")) {

                                    } else {
                                        for (int i = 0; i < originalInputArray.length; i++) {
                                            //and check to see if each one is a valid siteswap
                                            //numberOfObjectsInSiteswap(originalInputArray[i]);
                                            //I am not sure if we need to turn / and \ into blanks, maybe this is not needed, but
                                            //      it really does not matter i think. also, i am not sure if \\ is being taken as
                                            //      \ but i kind of think it might be.
                                            String siteswapName = originalInputArray[i].replace(" ", "").replace("/", "").replace("\\", "");

                                            //here we check to make sure that a line has at least 1 number or 1 letter before even trying to
                                            //      add it as a siteswap, it prevents crashes
                                            if (siteswapName.matches(".*\\d+.*") || siteswapName.matches(".*[a-zA-Z]+.*")) {
                                                if (numberOfObjectsInSiteswap(siteswapName) > 0) {

                                                    //we make the entry name by following this format @@##&&name@@##&&number@@##&&type@@##&&

                                                    //the name = ss:THE_SITESWAP


                                                    //we figure out the number based on the siteswap
                                                    String siteswapNumberOfObjects =
                                                            Integer.toString(numberOfObjectsInSiteswap(originalInputArray[i]));
                                                    // since it is valid, we are going to add a new entry

                                                    if (numberOfObjectsInSiteswap(originalInputArray[i]) <=
                                                            Integer.parseInt(getCellFromDB("SETTINGS", "MISC", "ID", "1")) &&
                                                            numberOfObjectsInSiteswap(originalInputArray[i]) > 0) {


                                                        //now we alphabetize the list
                                                        Collections.sort(listOfSplitObjTypeString, String.CASE_INSENSITIVE_ORDER);

                                                        for (int j = 0; j < listOfSplitObjTypeString.size(); j++) {

                                                            String siteswapEntryNameToAdd = buffer + "ss:" + siteswapName +
                                                                    buffer + siteswapNumberOfObjects +
                                                                    buffer + listOfSplitObjTypeString.get(j) + buffer;

                                                            //debugging
                                                            //Toast.makeText(MainActivity.this, "entryName = "+siteswapEntryNameToAdd, Toast.LENGTH_LONG).show();

                                                            //if it does not have an name empty, and if it is already in the
                                                            //          database, then we add it to the database
                                                            if (isValidInput(siteswapName) && !containsCaseInsensitive
                                                                    (siteswapEntryNameToAdd, getColumnFromDB("PATTERNS", "ENTRYNAME"))) {

                                                                //now we add our new siteswap pattern to the DB in the 'PATTERNS' table
                                                                addDataToDB("PATTERNS", "ENTRYNAME", siteswapEntryNameToAdd);

                                                                myDb.updateData("PATTERNS", "LINK", "ENTRYNAME", siteswapEntryNameToAdd,
                                                                        "www.siteswapbot.com/" + siteswapName);

                                                                myDb.updateData("PATTERNS", "SS", "ENTRYNAME", siteswapEntryNameToAdd, siteswapName);
                                                                numberOfSiteswapsAdded++;

                                                                //since we just added a new pattern to the DB, we want to update the mainPatternInputs with it
                                                                //Log.d("z", "8");
                                                                fillPatternMainInputsFromDB();

                                                                //I AM NOT SURE IF THIS IS NEEDED OR NOT
                                                                //clearAddPatternInputs();

                                                            } else {
                                                                numberOfSiteswapsFailed++;
                                                            }
                                                        }
                                                    }
                                                }
                                            } else {
                                                //since we have an invalid siteswap we want to keep track of the fact that 1 siteswap
                                                //  has failed for each object type we have.
                                                numberOfSiteswapsFailed = numberOfSiteswapsFailed + listOfSplitObjTypeString.size();
                                            }

                                        }
                                    }
                                    if (numberOfSiteswapsAdded > 0) {
                                        patternsATV.setText("ss:" + originalInputArray[0]);
                                        setSpinnerSelection(numOfObjsSpinner, Integer.toString(numberOfObjectsInSiteswap(originalInputArray[0])));
                                        setSpinnerSelection(objTypeSpinner, listOfSplitObjTypeString.get(0));
                                    }
                                    updateAddPatFromPatternsInputsSelected();

                                    Toast.makeText(MainActivity.this, Integer.toString(numberOfSiteswapsAdded) + " siteswaps added successfully. " +
                                            Integer.toString(numberOfSiteswapsFailed) + " failed.", Toast.LENGTH_LONG).show();

                                    dialog.dismiss();


                                } else {

                                    Toast.makeText(MainActivity.this, "Siteswap and/or prop type is empty.", Toast.LENGTH_LONG).show();

                                }
                            }
                        });
                    }
                });
                dialog.show();
            }
        });


        addPatternEntryName = (TextView) findViewById(R.id.addPatEntryName);
        addPatternEntryName.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showInfoDialog("Selected pattern", "This is the information on the currently selected pattern in the following" +
                        " format: name / number of objects / prop type. To select a pattern, use the the" +
                        " three inputs at the top of the screen. ");
                return false;
            }
        });

        TextView addPatDescriptionLabel = (TextView) findViewById(R.id.addPatDescriptionLabel);
        addPatDescriptionLabel.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showInfoDialog("Pattern description", "This is a place to input a description for the currently selected pattern.");
                return false;
            }
        });

        //the editText where the user inputs the pattern description
        addPatternDescriptionEditText = (EditText) findViewById(R.id.inoPatDescriptionEditText);
        addPatternDescriptionEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                //whenever this loses focus and there is something in the patternsATV, save what is in
                //      it to the database
                if (!hasFocus && !patternsATV.getText().toString().matches("")) {
                    updateDBfromAddInputs();
                }
            }
        });

        TextView addPatSiteswapLabel = (TextView) findViewById(R.id.addPatSiteswapLabel);
        addPatSiteswapLabel.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showInfoDialog("Siteswap", "This is a place to input the siteswap for the currently selected pattern. " +
                        "Siteswaps that don't average to a whole number will be confirmed before being added.");
                return false;
            }
        });

        //the editText where the user inputs the pattern siteswap
        addPatternSiteswapEditText = (EditText) findViewById(R.id.addPatSiteswapEditText);
        addPatternSiteswapEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                //whenever this loses focus and there is something in the patternsATV, save what is in
                //      it to the database
                if (!hasFocus &&
                        !patternsATV.getText().toString().matches("") &&
                        !addPatternSiteswapEditText.getText().toString().matches("")) {

                    if (numberOfObjectsInSiteswap(addPatternSiteswapEditText.getText().toString()) !=
                            Integer.parseInt(numOfObjsSpinner.getSelectedItem().toString().replaceAll("[^0-9]", ""))) {


                        new AlertDialog.Builder(MainActivity.this)
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .setTitle("Siteswap invalid")
                                .setMessage("'" + addPatternSiteswapEditText.getText().toString() + "' isn't a valid " +
                                        numOfObjsSpinner.getSelectedItem().toString() + " object siteswap. Add it anyway?")
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                        updateDBfromAddInputs();
                                    }

                                })
                                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                        //we need to load from DB
                                        updateAddPatFromPatternsInputsSelected();
                                    }

                                })
                                .show();
                    }

                }
            }
        });


        addPatLinkTextView = (TextView) findViewById(R.id.addPatLinkLabel);
        addPatLinkTextView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showInfoDialog("Pattern Link", "Input a link to any video or information on the currently selected pattern.");
                return false;
            }
        });

        addLinkGoButton = (Button) findViewById(R.id.addLinkGoButton);
        addLinkGoButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showInfoDialog("Open pattern link", "This will open a browser to the link below.");
                return true;
            }
        });
        addLinkGoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (addPatternLinkEditText.getText().toString().equals("")) {
                    Toast.makeText(MainActivity.this, "Link is blank.", Toast.LENGTH_LONG).show();

                } else {
                    String url = addPatternLinkEditText.getText().toString();

                    if (!url.startsWith("http://") && !url.startsWith("https://") && !url.startsWith("file://")) {
                        url = "http://" + url;
                    }


                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(browserIntent);
                }
            }
        });


        //the editText where the user inputs the pattern siteswap
        addPatternLinkEditText = (EditText) findViewById(R.id.addPatLinkEditText);
        addPatternLinkEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                //whenever this loses focus and there is something in the patternsATV, save what is in
                //      it to the database
                if (!hasFocus && !patternsATV.getText().toString().matches("")) {
                    updateDBfromAddInputs();


                }
            }
        });

        TextView addModifiersHeader = (TextView) findViewById(R.id.addModifiersHeader);
        addModifiersHeader.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showInfoDialog("Modifiers", "ModifiersHeaderDialog");
                return false;
            }
        });

        addModifierDeleteBut = (Button) findViewById(R.id.addModifierDeleteBut);
        addModifierDeleteBut.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showInfoDialog("Delete modifier", "This will delete the modifier that is currently selected below. The List of modifiers below " +
                        "is filled by selecting modifiers in the input that is second from the top of the screen.");
                return true;
            }
        });
        addModifierDeleteBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //before we try to make the confirmation dialog, we want to
                //  make sure that the modifiers selection isn't empty (thus nothing to delete)
                if (!addModifierNamesSpinner.getSelectedItem().toString().equals("")) {
//                    if (!modifierMATV.getText().toString().equals("") &&
//                        !addModifierNamesSpinner.getSelectedItem().toString().equals("")) {


                    //showPatterns(); //just used temporarily for debugging, shows the patterns in a message which have input to the db
                    //Toast.makeText(MainActivity.this, "Run", Toast.LENGTH_LONG).show();
                    new AlertDialog.Builder(MainActivity.this)
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setTitle("Deleting Modifier")
                            .setMessage("Are you sure you want to permanently delete modifier '" + addModifierNamesSpinner.getSelectedItem().toString() + "' ?")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    //remove it from our database
                                    myDb.deleteData("MODIFIERS", "NAME", addModifierNamesSpinner.getSelectedItem().toString());

                                    //use the databse to remove it from the modifierMATV unintentionalEnddown
                                    fillModifierMultiAutocompleteTextViewsFromDB();

                                    //use the database to remove it from the modifierMATV selected items
                                    updateModifierMATV();

                                    //update our modifiers Spinner from the modifierMATV selected items
                                    updateAddModifierFromModifierMATVselected();
//
                                    Toast.makeText(MainActivity.this, "Modifier deleted.", Toast.LENGTH_LONG).show();


                                }

                            })
                            .setNegativeButton("No", null)
                            .show();

                } else {
                    Toast.makeText(MainActivity.this, "There is no modifier to delete.", Toast.LENGTH_LONG).show();
                }
            }

        });

        addModifierNewBut = (Button) findViewById(R.id.addModifierNewBut);
        addModifierNewBut.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showInfoDialog("Add modifier", "This opens a box that can be used to add a new modifier to your database.");
                return true;
            }
        });
        addModifierNewBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //showPatterns(); //just used temporarily for debugging, shows the patterns in a message which have input to the db
                //Toast.makeText(MainActivity.this, "Run", Toast.LENGTH_LONG).show();

                View view = (LayoutInflater.from(MainActivity.this)).inflate(R.layout.addmodifierdialog, null);

                AlertDialog.Builder alertBuilder = new AlertDialog.Builder(MainActivity.this);
                alertBuilder.setView(view);
                final EditText newAddModifierInputDialog = (EditText) view.findViewById(R.id.newAddModifierInputDialogBox);
                final CheckBox newModifierSpecialThrowCheckBox = (CheckBox) view.findViewById(R.id.newModifierSpecialThrowCheckBox);
                newModifierSpecialThrowCheckBox.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        showInfoDialog("Special throw sequences", "If this box is checked, a modifier will be added for each of the special throw" +
                                " sequences. Special throw sequences are used with modifiers that only apply to specific throws, for instance" +
                                " to indicate that the modifier only applies to every other throw, the special sequece 'yn' would be used because" +
                                " 'yes', the first throw is special, and 'no' the second throw is not. For a more detailed explanation of" +
                                " special throw sequences, touch the 'MODIFIERS' header in the 'ADD' tab for 2 seconds and scroll down to the 'SPECIAL" +
                                " THROW SEQUENCES' section. To add or remove special throw sequences, use the 'SET' tab.");
                        return true;
                    }
                });

                alertBuilder.setCancelable(true)
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        });

                Dialog dialog = alertBuilder.create();
                dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(final DialogInterface dialog) {
                        Button button = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                        button.setOnClickListener(new View.OnClickListener() {

                            @Override
                            public void onClick(View view) {

//since the ok button was just clicked, we want to add whatever the user has input

                                updateDBfromAddInputs();

                                String s = newAddModifierInputDialog.getText().toString();
                                int hyphenCounter = 0;
                                for (int i = 0; i < s.length(); i++) {
                                    if (s.charAt(i) == '-') {
                                        hyphenCounter++;
                                    }
                                }
                                if (hyphenCounter > 1) {
                                    Toast.makeText(MainActivity.this, "Modifiers can not contain more than one '-'.", Toast.LENGTH_LONG).show();
                                } else if (newModifierSpecialThrowCheckBox.isChecked() && hyphenCounter == 1) {
                                    Toast.makeText(MainActivity.this, "When the 'special throw sequences' checkbox is checked, modifiers can not " +
                                            "contain the symbol '-'", Toast.LENGTH_LONG).show();
                                } else {
                                    int numberOfExtraModifiersToAdd = 0;
                                    if (newModifierSpecialThrowCheckBox.isChecked()) {
                                        setSpecialThrowSequenceList();
                                        numberOfExtraModifiersToAdd = setSequenceForSpinner.size();
                                    }
                                    for (int i = -1; i < numberOfExtraModifiersToAdd; i++) {

                                        String modifierToAdd = newAddModifierInputDialog.getText().toString();

                                        if (i > -1) {
                                            modifierToAdd = newAddModifierInputDialog.getText().toString() + "-" + setSequenceForSpinner.get(i);
                                        }
                                        //but if the dialog is submited empty, no modifier is added
                                        if (isValidInput(modifierToAdd)) {

                                            //or if it is a duplicate, no modifier is added
                                            if (containsCaseInsensitive(modifierToAdd, getColumnFromDB("MODIFIERS", "NAME"))) {
                                                if (i == -1) {
                                                    Toast.makeText(MainActivity.this, "Modifier already exists.", Toast.LENGTH_LONG).show();
                                                }
                                            } else {

                                                //we want to replace any ',' with a '.'
                                                if (modifierToAdd.contains(",")) {
                                                    modifierToAdd = modifierToAdd.replace(",", ".") + " ";
                                                    if (i == -1) {
                                                        Toast.makeText(MainActivity.this, "Modifiers can't contain the symbol ',' it was replaced with '.'", Toast.LENGTH_LONG).show();
                                                    }
                                                }

                                                //but if it isn't empty or a duplicate, we add it to the modifiers list in the DB


                                                //give the focus to the description editText so that a description can be easily added/read
                                                addModifierDescriptionEditText.requestFocus();


                                                //before we add anything, we want to clean up what is in our modifierMATV by getting rid of
                                                //     any spaces that may be at the end and then give it just the 1 space we want
                                                modifierMATV.setText(modifierMATV.getText().toString().replaceFirst("\\s++$", "") + " ");

                                                //but if the string is nothing but one empty space, then get rid of that space so there
                                                //  is not an empty space before the first modifier
                                                if (modifierMATV.getText().toString().equals(" ")) {
                                                    modifierMATV.setText("");
                                                }

                                                //now we add our new modifier to the DB
                                                addDataToDB("MODIFIERS", "NAME", modifierToAdd);
                                                //and add it to the modifiers ATVs from the DB
                                                fillModifierMultiAutocompleteTextViewsFromDB();


                                                //we only want to add the modifier to the modifierMATV if it is not one that just got added
                                                //      with a bunch of special throw sequence endings
                                                if (i == -1) {
                                                    //change the selected string in the main modifierMATV
                                                    modifierMATV.setText(modifierMATV.getText().toString() + modifierToAdd + ", ");

                                                    histModifierCombinationOtherThanExactlyCurrentCheckbox.setText("Modifier combination other than exactly '" + modifierMATV.getEditableText().toString() + "'");
                                                    histModifierCombinationThatDoesntContainCurrentCheckbox.setText("Modifier combination that doesn't contain '" + modifierMATV.getEditableText().toString() + "'");
                                                    fillHistoryRecordsTextView();

                                                    //and make a toast explaining what just happened
                                                    Toast.makeText(MainActivity.this, "Modifier '" + newAddModifierInputDialog.getText().toString() + "' added", Toast.LENGTH_LONG).show();

                                                }


                                                //and add the new modifier name to the addModifierNamesSpinner from the modifierMATV selected items
                                                updateAddModifierFromModifierMATVselected();


                                            }
                                        }
                                    }
                                    //THIS IS JUST FOR DEBUGGING
                                    //Toast.makeText(MainActivity.this, newAddModifierInputDialog.getText().toString(), Toast.LENGTH_LONG).show();
                                    //Dismiss once everything is OK.
                                    dialog.dismiss();
                                }

                            }
                        });
                    }
                });
                dialog.show();
            }
        });

        TextView addModifierDescriptionLabel = (TextView) findViewById(R.id.addModifierDescriptionLabel);
        addModifierDescriptionLabel.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showInfoDialog("Modifier description", "This is where you can input a description for the currently selected modifier. A modifier is " +
                        "any variation that can be applied to many different patterns, such as mills mess or backcrosses or " +
                        "and situation you can be in while juggling, such as watching a movie or listening to music, or anything " +
                        "that you can be doing while juggling, such as having your eyes closed or balancing on a rola bola.");
                return false;
            }
        });

        //the editText where the user inputs the pattern siteswap
        addModifierDescriptionEditText = (EditText) findViewById(R.id.inoModifierDescriptionEditText);
        addModifierDescriptionEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {

                    //every time the description editText loses focus, we want to save what is in the description editText to the DB

                    //but we only want to do this is there is something in the spinner
                    if (addModifierNamesSpinner.getCount() > 0) {
                        //and then we only want to do anything if we are not selecting an empty spinner item
                        if (!addModifierNamesSpinner.getSelectedItem().toString().equals("")) {

                            updateDBfromAddInputs();


                        }
                    }
                }
            }
        });


        //this makes the spinner that is used to choose the name of the modifier we want to change
        addModifierNamesSpinner = (Spinner) findViewById(R.id.addModifierNameSpinner);
        ArrayAdapter<String> addModifierNamesSpinnerAdapter = new ArrayAdapter<>(this, android.R.layout.select_dialog_item, addModifierNamesForSpinner);
        addModifierNamesSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        addModifierNamesSpinner.setAdapter(addModifierNamesSpinnerAdapter);
        addModifierNamesSpinner.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showInfoDialog("Modifier name", "This is a list of all the modifiers currently selected in the upper part of the screen. Selecting a" +
                        " modifier from this list allows you to edit it's description or delete it from your database.");
                return true;
            }
        });
        addModifierNamesSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //update what is in the description editText based on what is in the DB
                if (!addModifierNamesSpinner.getSelectedItem().toString().equals("")) {
                    //save what is in the description editText to the DB
                    //myDb.updateData("MODIFIERS","DESCRIPTION","NAME",addModifierNamesSpinner.getSelectedItem().toString(),
                    //       addModifierDescriptionEditText.getText().toString());

                    //load the description from the DB based on what is selected in the spinner
                    addModifierDescriptionEditText.setText(getCellFromDB("MODIFIERS", "DESCRIPTION", "NAME", addModifierNamesSpinner.getSelectedItem().toString()));

                    //give the focus to the description editText so that a description can be easily read/added
                    addModifierDescriptionEditText.requestFocus();
                }

                //Toast.makeText(getBaseContext(),parent.getItemAtPosition(position)+" selected",Toast.LENGTH_LONG).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }


        });

        addModifierNamesSpinner.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                //we only want to do anything if there are any items even in our spinner
                if (addModifierNamesSpinner.getCount() > 0) {
                    //and then we only want to do anything if we are not selecting an empty spinner item
                    if (!addModifierNamesSpinner.getSelectedItem().toString().equals("")) {
                        //save what is in the description editText to the DB
                        myDb.updateData("MODIFIERS", "DESCRIPTION", "NAME", addModifierNamesSpinner.getSelectedItem().toString(),
                                addModifierDescriptionEditText.getText().toString());


                    }
                }
                //updateModifierMATV();
                //updateAddModifierFrommodifierMATVselected();
                return false;
            }

        });

//        addModifierNamesSpinner.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//            @Override
//            public void onFocusChange(View v, boolean hasFocus) {
//                if(!addModifierNamesSpinner.getSelectedItem().toString().equals("") &&
//                        !addModifierNamesSpinner.getSelectedItem().toString().equals(null)) {
//                    //save what is in the description editText to the DB
//                    myDb.updateData("MODIFIERS", "DESCRIPTION", "NAME", addModifierNamesSpinner.getSelectedItem().toString(),
//                            addModifierDescriptionEditText.getText().toString());
//                }
//            }
//        });

//        addModifierNamesSpinner.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                //when the spinner is selected, if it isn't empty...
///*                if(!addModifierNamesSpinner.getSelectedItem().toString().equals("") &&
//                        !addModifierNamesSpinner.getSelectedItem().toString().equals(null)) {
//                    //save what is in the description editText to the DB
//                    myDb.updateData("MODIFIERS", "DESCRIPTION", "NAME", addModifierNamesSpinner.getSelectedItem().toString(),
//                            addModifierDescriptionEditText.getText().toString());
//                }*/
//            }
//        });

        //now that we have everything we need for the structure of the add tab,
        //      we can go ahead and populate stuff from our database

        updateAddPatternNumberOfObjectsFromDB(); //uses the number in the settings table of the database that indicates the
        //                                          max number of props to be used to populate the selection for prop number

    }


    public void showInfoDialog(String title, String infoToShow) {
        if (infoToShow.contains("ModifiersHeaderDialog")) {
            View view = (LayoutInflater.from(MainActivity.this)).inflate(R.layout.showmodifiersinfodialog, null);

            AlertDialog.Builder alertBuilder = new AlertDialog.Builder(MainActivity.this);
            alertBuilder.setView(view)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle(title + "\n");

            TextView modifiersDialogUpperText = (TextView) view.findViewById(R.id.modifiersDialogUpperText);
            modifiersDialogUpperText.setText("\nIn this section, you can view and edit the notes on any of the modifiers currently selected" +
                    " in the 'Main Input' at the top of the screen.\n\nModifiers are any variation of a juggling pattern (mills mess), special" +
                    " throw (backcross), anything you can do in addition (blind, balance on a rola bola), or any situation you can be in while" +
                    " juggling (watching a movie, being in cold weather). Any number of modifiers can be selected, including none. They " +
                    " will be used in conjunction with the pattern selected above to keep track of your juggling runs from the 'JUG'" +
                    " tab. Summaries of each run can be viewed in the 'HIST' tab, or by exporting your database.\n\n" +
                    "SPECIAL THROW SEQUENCES\n" +
                    "To indicate that only some of the throws are special, such as 'every other throw is a backcross', use 'bx' for" +
                    " backcross, and then (yn) to show that, yes, the first throw is a bx, and then, no, the second throw is not, bx(yn). If you wanted to show that" +
                    " every 5th throw is an 'outside throw', then it would be 'out(ynnnn)'. For patterns with synchronous throws, like ss:(6,4), if we want to" +
                    " show that the 6s are shoulder throws, we use st(y.n). Note that the comma is replaced with a period because modifiers cannot have commas" +
                    " in them. Multiple modifiers can be used at the same time, to indicate that we are doing ss:(6,4) with the 6s as shoulder throws and the" +
                    " 4s as 'under the leg', the pattern would be ss:(6,4) and the modifers would be 'st(y.n), ul(n.y)'.\n\n" +
                    "Below is a list of suggested abbreviations.\n\n" +
                    "               ADDITIONS \n" +
                    "fbal - face balance \n" +
                    "bnc - head bounce \n" +
                    "rol - rola bola \n" +
                    "uni - unicycle \n" +
                    "stg - on stage \n" +
                    "lydn - lying down \n\n" +
                    "              VARIATIONS \n" +
                    "mm - Mills Mess \n" +
                    "rr - Rubenstein's Revenge \n" +
                    "bb - Burke's Barrage \n" +
                    "bm - Boston Mess \n" +
                    "windmill - Windmill \n" +
                    "ee - Eric's Extension \n\n" +
                    "            SPECIAL THROWS \n" +
                    "out - outside throw \n" +
                    "col - columns \n" +
                    "clw - clawed \n" +
                    "bx - back crosses \n" +
                    "rbx - reverse back crosses \n" +
                    "st - shoulder throws \n" +
                    "rst - reverse shoulder throws \n" +
                    "ua - under the arm\n" +
                    "ul - under the leg\n" +
                    "btn - behind the neck throws \n" +
                    "chops - chops \n" +
                    "rchops - reverse chops \n" +
                    "pen - penguin intentionalEndes \n" +
                    "oh - overhead \n" +
                    "x - with crossed arms \n" +
                    "con - contortion \n" +
                    "halfcon - half contortion \n" +
                    "bl - blind \n" +
                    "bbb - behind the back blind \n" +
                    "bbl - behind the back looking \n" +
                    "frk - forked\n" +
                    "frc - forced bounce\n" +
                    "alb - Alberts \n" +
                    "tre - Treblas \n" +
                    "flt - flats \n" +
                    "slp - slapbacks \n" +
                    "ff - flat fronts\n" +
                    "pan - pancakes \n" +
                    "pdn - pulldown \n\n" +
                    "             Club spins:\n" +
                    "cs0 - flats \n" +
                    "cs1 - singles \n" +
                    "cs2 - doubles \n" +
                    "cs0.5 - half spins \n" +
                    "cs-x1 - reverse singles \n" +
                    "cs3cs1 - triple singles \n" +
                    "cs0cs1cs2cs3cs4 - flat single double triple quads\n");

            TextView modifiersDialogLowerText = (TextView) view.findViewById(R.id.modifiersDialogLowerText);
            modifiersDialogLowerText.setText("This List is a slightly altered version of www.jugglingedge.com/help/tossformat.php\n" +
                    "Also at jugglingedge is a large list of records.");
            modifiersDialogLowerText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    askIfWantToCopyToClipboard("www.jugglingedge.com/help/tossformat.php");
                }
            });
            modifiersDialogLowerText.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    askIfWantToCopyToClipboard("www.jugglingedge.com/help/tossformat.php");
                    return false;
                }
            });


            alertBuilder.setCancelable(true)
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });

            Dialog dialog = alertBuilder.create();
            dialog.show();
        } else if (title.equals("Patterns")) {
            new AlertDialog.Builder(MainActivity.this)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle(title)
                    .setMessage(infoToShow)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }


                    })

                    .show();
        } else {
            new AlertDialog.Builder(MainActivity.this)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle(title)
                    .setMessage(infoToShow)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }


                    })
                    .show();
        }
    }

    public void showPatternsWithHistoryDialog() {

        View view = (LayoutInflater.from(MainActivity.this)).inflate(R.layout.patternswithhistorydialog, null);

        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(MainActivity.this);
        alertBuilder.setView(view);
        final TextView patternsWithHistorySortTV = (TextView) view.findViewById(R.id.patternsWithHistorySortTV);
        final Spinner patternsWithHistorySortSpinner = (Spinner) view.findViewById(R.id.patternsWithHistorySortSpinner);
        List<String> patternsWithHistorySortSpinnerList = new ArrayList<>(); //our list of patterns
        patternsWithHistorySortSpinnerList.add(0, "Alphabetical");
        patternsWithHistorySortSpinnerList.add(1, "Days since initial run");
        patternsWithHistorySortSpinnerList.add(2, "Days since used");
        patternsWithHistorySortSpinnerList.add(3, "Days since personal best");
        patternsWithHistorySortSpinnerList.add(4, "Length");
        ArrayAdapter<String> patternsWithHistorySortSpinnerAdapter = new ArrayAdapter<>(MainActivity.this,
                android.R.layout.select_dialog_item, patternsWithHistorySortSpinnerList);
        patternsWithHistorySortSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        patternsWithHistorySortSpinner.setAdapter(patternsWithHistorySortSpinnerAdapter);
        final TextView patternsWithHistoryPatternsTV = (TextView) view.findViewById(R.id.patternsWithHistoryPatternsTV);
        final Spinner patternsWithHistoryPatternsSpinner = (Spinner) view.findViewById(R.id.patternsWithHistoryPatternsSpinner);

        patternsWithHistorySortSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                fillPatternsWithHistoryPatternsSpinner(position, patternsWithHistoryPatternsSpinner);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


//now we need to populate our first spinner, and then based on what is selected in the first spinner, we set the order of the
        //      items in the 2nd spinner

        alertBuilder.setCancelable(true)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (patternsWithHistoryPatternsSpinner.getCount()>0) {
                            patternsATV.setText(patternsWithHistoryPatternsSpinner.getSelectedItem().toString().split("\\(")[0]);
                        }
                    }
                });

        Dialog dialog = alertBuilder.create();

        dialog.show();


        //NO! we want the info mentioned above, but in this format:
        //  TV: sorted by
        //  Spinner: differnt choices to sort
        //  TV: patterns
        //  Spinner: patterns(secondary info)
    }

    public void fillPatternsWithHistoryPatternsSpinner(int position, Spinner patternSpinner) {
        View view = (LayoutInflater.from(MainActivity.this)).inflate(R.layout.patternswithhistorydialog, null);

        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(MainActivity.this);
        alertBuilder.setView(view);
        final Spinner patternsWithHistoryPatternsSpinner = (Spinner) view.findViewById(R.id.patternsWithHistoryPatternsSpinner);
        //our list that will be put in the pattern spinner
        List<String> patternsWithHistoryPatternsSpinnerList = new ArrayList<>();
        List<String> patternsWithHistoryPatterns = new ArrayList<>();
        List<String> patternsWithHistorySecondaryInfo = new ArrayList<>();
        int numberOfPatterns = myDb.getFromDB("HISTORYENDURANCE").getColumnCount();

//        final Spinner patternsWithHistorySortSpinner = (Spinner) view.findViewById(R.id.patternsWithHistorySortSpinner);
//        List<String> patternsWithHistorySortSpinnerList = new ArrayList<>(); //our list of patterns
//        patternsWithHistorySortSpinnerList.add(0,"0");
//        ArrayAdapter<String> patternsWithHistorySortSpinnerAdapter = new ArrayAdapter<>(MainActivity.this,
//                android.R.layout.select_dialog_item, patternsWithHistorySortSpinnerList);
//        patternsWithHistorySortSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        patternsWithHistorySortSpinner.setAdapter(patternsWithHistorySortSpinnerAdapter);
//

        //based on the position of the selected spinner item, we need to fill our list, patternsWithHistoryPatternsSpinnerList,
        //  as well as add the secondary information to it if it has any

        if (position == 0) {//Alphabetical
            //Log.d("sortPattern", "Alphabetical");
            //the reason we start at 2 is because 0 is the id and 1 is the 'modifiers column, we just want 2 and beyond
            for (int i = 2; i < numberOfPatterns; i++) {

                //we only want to add pattern names that have the same number of objs and prop type that is currently
                //      selected in the main inputs
                ////Log.d("checkHereA", myDb.getFromDB("HISTORYENDURANCE").getColumnName(i).split(buffer)[2]);
                ////Log.d("checkHereB", numOfObjsSpinner.getSelectedItem().toString());
                if (myDb.getFromDB("HISTORYENDURANCE").getColumnName(i).split(buffer)[2].equals(numOfObjsSpinner.getSelectedItem().toString()) &&
                        myDb.getFromDB("HISTORYENDURANCE").getColumnName(i).split(buffer)[3].equals(objTypeSpinner.getSelectedItem().toString())) {
                    //Log.d("checkHereC", Integer.toString(i));
                    if (entryInfo(myDb.getFromDB("HISTORYENDURANCE").getColumnName(i), "initialDate").equals("01/01/31 01:01:01") ||
                            entryInfo(myDb.getFromDB("HISTORYENDURANCE").getColumnName(i), "initialDate").equals("01/01/01 01:01:01")) {
                        Log.d("rejectedA", myDb.getFromDB("HISTORYENDURANCE").getColumnName(i));
                    }else{
                        patternsWithHistoryPatterns.add(myDb.getFromDB("HISTORYENDURANCE").getColumnName(i).split(buffer)[1]);
                    }

                }
                ////Log.d("sortPattern", patternsWithHistoryPatterns.get(i-2));
            }
            //put the list of patterns into the list that we will populate the spinner with, in this instance they are the
            //      same because there is not secondary information to add to the, but in the other ways we sort the
            //      stuff in the spinner there wil be secondary information
            patternsWithHistoryPatternsSpinnerList = patternsWithHistoryPatterns;
            //alphabetize the list
            Collections.sort(patternsWithHistoryPatternsSpinnerList, String.CASE_INSENSITIVE_ORDER);


        } else if (position == 1 || position == 2 || position == 3) {//Days since initial run OR Days since used
            //the reason we start at 2 is because 0 is the id and 1 is the 'modifiers column, we just want 2 and beyond
            for (int i = 2; i < numberOfPatterns; i++) {
                //Log.d("initialDate", Integer.toString(position));
                if (myDb.getFromDB("HISTORYENDURANCE").getColumnName(i).split(buffer)[2].equals(numOfObjsSpinner.getSelectedItem().toString()) &&
                        myDb.getFromDB("HISTORYENDURANCE").getColumnName(i).split(buffer)[3].equals(objTypeSpinner.getSelectedItem().toString())) {

//this is a complicated way of checking to see if there are any history items for this column
                    if (!entryInfo(myDb.getFromDB("HISTORYENDURANCE").getColumnName(i), "initialDate").equals("01/01/31 01:01:01") &&
                            !entryInfo(myDb.getFromDB("HISTORYENDURANCE").getColumnName(i), "initialDate").equals("01/01/01 01:01:01")) {


                        String infoType = "";
                        patternsWithHistoryPatterns.add(myDb.getFromDB("HISTORYENDURANCE").getColumnName(i).split(buffer)[1]);
                        if (position == 1) {
                            infoType = "initialDate";
                            //Log.d("initialDate", "initialDate");
                        }
                        if (position == 2) {
                            infoType = "mostRecentDate";
                        }
                        if (position == 3) {
                            infoType = "daysSincePB";

                        }

                        //this fills our list of secondary info with the patterns and secondary info that we need based on which
                        //  type has been selected by the user

                        patternsWithHistorySecondaryInfo.add(entryInfo(myDb.getFromDB("HISTORYENDURANCE").getColumnName(i), infoType));
                        Log.d("entryInfoAdded", entryInfo(myDb.getFromDB("HISTORYENDURANCE").getColumnName(i), infoType));
                    }
                }
                //Log.d("sortPattern", "sort");
            }
            //put the list of patterns into the list that we will populate the spinner with
            if (patternsWithHistoryPatterns.size() > 0) {
                for (int i = 0; i < patternsWithHistoryPatterns.size(); i++) {
                    String earliestDate = "01/01/30 01:01:01";
                    int indexOfEarliestDate = -1;
                    for (int j = 0; j < patternsWithHistorySecondaryInfo.size(); j++) {

                        String secondaryInfoToUse = patternsWithHistorySecondaryInfo.get(j).replace(")", "");

                        if (i > 0) {//if this is not the first one we are adding to the list, then we want
                            //      to make sure that we are finding the earliest one after the last one that
                            //      we just added
//                            for(int h = 0; h<patternsWithHistoryPatterns.size();h++){
//                                Log.d("terns", patternsWithHistoryPatterns.get(h));
//
//                            }
//                            for(int h = 0; h<patternsWithHistorySecondaryInfo.size();h++){
//                                Log.d("info", patternsWithHistorySecondaryInfo.get(h));
//
//                            }
//                            for(int h = 0; h<patternsWithHistoryPatternsSpinnerList.size();h++){
//                                Log.d("list", patternsWithHistoryPatternsSpinnerList.get(h));
//
//                            }
                            if (isDateAfterDateOrEqual(earliestDate, secondaryInfoToUse) &&
                                    !earliestDate.equals(secondaryInfoToUse) &&
                                    isDateAfterDateOrEqual(secondaryInfoToUse,
                                            patternsWithHistoryPatternsSpinnerList.get(patternsWithHistoryPatternsSpinnerList.size()-1).split("\\(")[1].replace(")", ""))
                                    && !secondaryInfoToUse.equals(
                                    patternsWithHistoryPatternsSpinnerList.get(patternsWithHistoryPatternsSpinnerList.size()-1).split("\\(")[1].replace(")", ""))) {

                                earliestDate = secondaryInfoToUse;
                                indexOfEarliestDate = j;
                                //Log.d("inhereA", earliestDate);
                            }
                        } else {//since this is the first one we are adding, we just have to find what the earliest one is
                            if (isDateAfterDateOrEqual(earliestDate, secondaryInfoToUse)
                                    && !earliestDate.equals(secondaryInfoToUse)) {
                                earliestDate = secondaryInfoToUse;
                                indexOfEarliestDate = j;
                                //Log.d("inhereB", earliestDate);
                            }
                        }

                    }
                    //now that we have the earliest initial that has not been added to our list that will
                    //  be used in our spinner, we go ahead and add it to the list
                    if (indexOfEarliestDate!=-1) {
                        patternsWithHistoryPatternsSpinnerList.add(
                                patternsWithHistoryPatterns.get(indexOfEarliestDate).replace(")", "")
                                        + "(" + patternsWithHistorySecondaryInfo.get(indexOfEarliestDate).replace(")", "") + ")");
                    }
                }
            }

        } else if (position == 4) { //this sorts the patterns based on length of current PB


            //Log.d("position", "4");
            //this fills our list with the patterns and secondary info that we need based on which
            //  type has been selected by the user

            for (int i = 2; i < numberOfPatterns; i++) {
                if (myDb.getFromDB("HISTORYENDURANCE").getColumnName(i).split(buffer)[2].equals(numOfObjsSpinner.getSelectedItem().toString()) &&
                        myDb.getFromDB("HISTORYENDURANCE").getColumnName(i).split(buffer)[3].equals(objTypeSpinner.getSelectedItem().toString())) {

                    //this is a complicated way of checking to see if there are any history items for this column
                    if (!entryInfo(myDb.getFromDB("HISTORYENDURANCE").getColumnName(i), "initialDate").equals("01/01/31 01:01:01") &&
                            !entryInfo(myDb.getFromDB("HISTORYENDURANCE").getColumnName(i), "initialDate").equals("01/01/01 01:01:01")) {


                        String infoType = "";
                        patternsWithHistoryPatterns.add(myDb.getFromDB("HISTORYENDURANCE").getColumnName(i).split(buffer)[1]);
                        patternsWithHistorySecondaryInfo.add(entryInfo(myDb.getFromDB("HISTORYENDURANCE").getColumnName(i), "PBLength"));
                    }
                }

            }
            int highestUsedPB = 0;

            //put the list of patterns into the list that we will populate the spinner with
            //Log.d("terns.size()", Integer.toString(patternsWithHistoryPatterns.size()));
            if (patternsWithHistoryPatterns.size() > 0) {
                for (int i = 0; i < patternsWithHistoryPatterns.size(); i++) {
                    int shortestEligiblePB = 99999999;
                    int indexOfEarliestDate = -1;
                    for (int j = 0; j < patternsWithHistoryPatterns.size(); j++) {

                        int lengthOfPB = Integer.parseInt(patternsWithHistorySecondaryInfo.get(j).replace(")", ""));


//                        //Log.d("lengthOfPB", Integer.toString(lengthOfPB));
//                        //Log.d("shortestPB", Integer.toString(shortestEligiblePB));
//                        //Log.d("highestUsedPB", Integer.toString(highestUsedPB));
//                        //Log.d("pattern", patternsWithHistoryPatterns + "(" +
//                                Integer.toString(lengthOfPB) + ")");
//                        for (int z = 0; z<patternsWithHistoryPatternsSpinnerList.size(); z++){
//                            //Log.d("list", patternsWithHistoryPatternsSpinnerList.get(z));
//                        }

                        //we want to find the shortest PB that belongs to a pattern which has not been added yet
                        if (lengthOfPB < shortestEligiblePB &&
                                lengthOfPB >= highestUsedPB &&
                                !patternsWithHistoryPatternsSpinnerList.contains(patternsWithHistoryPatterns.get(j) + "(" +
                                        formattedTime(lengthOfPB) + ")")) {

                            shortestEligiblePB = lengthOfPB;
                            indexOfEarliestDate = j;
                            //Log.d("shortestPBchng", Integer.toString(shortestEligiblePB));


                        }

                    }
                    if (indexOfEarliestDate != -1) {
                        //now that we have the earliest initial that has not been added to our list that will
                        //  be used in our spinner, we go ahead and add it to the list

                        String formattedLength = formattedTime(Integer.parseInt(
                                patternsWithHistorySecondaryInfo.get(indexOfEarliestDate).replace(")", "")));

                        patternsWithHistoryPatternsSpinnerList.add(
                                patternsWithHistoryPatterns.get(indexOfEarliestDate).replace(")", "")
                                        + "(" + formattedLength + ")");
                        highestUsedPB = shortestEligiblePB;
                    }
                }

            }


        }
        ArrayAdapter<String> patternsWithHistoryPatternsSpinnerAdapter = new ArrayAdapter<>(MainActivity.this,
                android.R.layout.select_dialog_item, patternsWithHistoryPatternsSpinnerList);
        //patternsWithHistoryPatternsSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        patternSpinner.setAdapter(patternsWithHistoryPatternsSpinnerAdapter);

//
//        Dialog dialog = alertBuilder.create();
//
//        dialog.show();
    }

    public String entryInfo(String entryName, String infoType) {
        //Log.d("entryName", entryName);

        //Possible info types:
        //      -initialDate
        //      -mostRecentDate
        //      -daysSincePB
        //      -PBLength

        //First we go through all of the endur records of the pattern
        //for (int i = 0; i < )
        int infoInt = 0;
        String infoString = "";
        if (infoType.equals("initialDate")) {
            infoString = "01/01/31 01:01:01";
        }
        if (infoType.equals("mostRecentDate") || infoType.equals("daysSincePB")) {
            infoString = "01/01/01 01:01:01";
        }
        if (infoType.equals("PBLength") || infoType.equals("daysSincePB")) {
            infoString = "";
        }

//        //this cycles through every column of of history endurance table, it contains the information on every endurance run
//        //  that has happened. We skip the 1st clumn because it is the names of the different combinations of modifiers.
//        for (int i = 2; i < myDb.getFromDB("HISTORYENDURANCE").getColumnCount(); i++) {
//            //since we are going through the table 1 column at a time, we want to get the column name of the column that we are
//            //  currently dealing with
//            String columnName = myDb.getFromDB("HISTORYENDURANCE").getColumnName(i);

        //now we use that column name to fill up this list with each cell of the column. 1 cell = 1 item in the list. inside of a cell
        //  is all the information for each modifier/pattern combination. the format for endurance entries is:
        // (end explanation)(intentionalEnd/unintentionalEnd)(run length)(date/time), as many entries as needed are hed in a single cell, the format just
        //  repeats over and over.
        List<String> listOfAllCellsInTheColumn = getColumnFromDB("HISTORYENDURANCE", entryName);


        //now we cycle through each of the list items(the cells in our column), each one of these represents a possible different unique
        //  combination of modifiers & pattern. If it contains anything, then we know it is indeed a unique combination, once we know
        //  that, we want to find out if it contains a run that ended in a intentionalEnd and/or unintentionalEnd
        for (int j = 0; j < listOfAllCellsInTheColumn.size(); j++) {
            //Toast.makeText(getBaseContext(), listOfAllCellsInDB.get(j), Toast.LENGTH_LONG).show();


            //here we check to make sure that the list item(cell) is not null or empty, it has to have something in it or we don't
            //  want anything to do with it
            if (listOfAllCellsInTheColumn.get(j) != null && !listOfAllCellsInTheColumn.get(j).isEmpty()) {

                //now we split the item(cell) on the parenthesis and put all the split parts into a list
                String[] splitListOfAllCellsInDB = listOfAllCellsInTheColumn.get(j).split("\\)\\(");


                //Here we are setting off to go gather all the endurance runs that ended intentionally and
                //      we are counting them, and seeing how many days it has been since them, and how long we
                //      spent in them

                //we go through each item in the list
                for (int k = 0; k < splitListOfAllCellsInDB.length; k++) {

                    //we only care about the dates since we are looking for the easrliest dte(the initial date
                    if (k % 4 == 3) {
                        //if we find a date earlier than the current earliest date that we have, we go ahead and change our current
                        //      earliest date(infoString)
                        if (infoType.equals("initialDate")) {
                            //Log.d("dateX", infoString);
                           // Log.d("dateY", splitListOfAllCellsInDB[k].replace(")", ""));
                            if (isDateAfterDateOrEqual(infoString, splitListOfAllCellsInDB[k]) &&
                                    !infoString.equals(splitListOfAllCellsInDB[k])) {
                                //Log.d("chaneToY", splitListOfAllCellsInDB[k]);
                                infoString = splitListOfAllCellsInDB[k];
                            }
                        }
                        if (infoType.equals("mostRecentDate")) {
                           // Log.d("dateA", infoString);
                           // Log.d("dateB", splitListOfAllCellsInDB[k].replace(")", ""));
                            if (isDateAfterDateOrEqual(splitListOfAllCellsInDB[k], infoString) &&
                                    !infoString.equals(splitListOfAllCellsInDB[k])) {
                              //  Log.d("chaneTo", splitListOfAllCellsInDB[k]);
                                infoString = splitListOfAllCellsInDB[k];
                            }
                        }
                        //we need to find the longest length and then take the date from that length
                        //  right now, the stuff in the 2 conditionals below is not correct at all, just copied
                        //      and pasted from above.
                        //  I think the first thing to do is find the longest length
                        if (infoType.equals("daysSincePB") || infoType.equals("PBLength")) {
/*                            //Log.d("dateA", infoString);
                            //Log.d("dateB", splitListOfAllCellsInDB[k].replace(")",""));*/

                            //we need to subrtact whatever we need to from k to get the length,
                            //      keep track of it and keep comparing it to the current winner.
                            //      IT WILL BE A SIMILAR KING OF THE HILL TYPE THING AS BELOW

                            if (Integer.parseInt(splitListOfAllCellsInDB[k - 1]) > infoInt) {
                                infoInt = Integer.parseInt(splitListOfAllCellsInDB[k - 1]);
                                if (infoType.equals("daysSincePB")) {
                                    infoString = splitListOfAllCellsInDB[k];
                                }
                                if (infoType.equals("PBLength")) {
                                    infoString = splitListOfAllCellsInDB[k - 1];
                                }
                            }
                        }


//                        String endType = splitListOfAllCellsInDB[k];
//
//                        //it is set up so that all the stats that are for 'unintentionalEnd' are in 'arrayToReturn' indexes that are 100 higher than
//                        //  their 'intentionalEnd' equivalents, so here we check to see which we are dealing with, and set our 'indexAdder'
//                        if (endType.equals("intentionalEnd") || endType.equals("unintentionalEnd")) {
//
//                            //this is getting the time that the run actually ended, it is its begin time plus its length
//                            String endDateAndTimeOfThisEndur = addTimeToStringDate("SECOND",
//                                    Integer.parseInt(splitListOfAllCellsInDB[k + 1]), splitListOfAllCellsInDB[k + 2]);
//
//
//                            //this is getting how many seconds since doing an endurance run that ended in a intentionalEnd. If we find a run
//                            //  that is more recent then our currently most recent intentionalEnd run, then we update the most recent intentionalEnd
//                            //  run to whatever the more recent one was.
//                            if (secondsSinceMostRecentRun > timeSince("seconds", endDateAndTimeOfThisEndur)) {
//                                secondsSinceMostRecentRun = timeSince("seconds", endDateAndTimeOfThisEndur);
//                                dateAndTimeOfMostRecentRun = endDateAndTimeOfThisEndur;
//                                //Log.d("dateAndTimeOfMost..", endDateAndTimeOfThisEndur);
//                            }
//                        }
                    }
                }
            }
        }
//now we deal with the drill table, this is only for the 'initialDate' and 'mostRectDate'
        if (infoType.equals("initialDate") || infoType.equals("mostRecentDate")) {

            //at this point 'minutesSinceMostRecentRun' is set as the end time of the most recent endurance run, now we check to see if there
            //      is a more recent drill run
//        for (int i = 2; i < myDb.getFromDB("HISTORYDRILL").getColumnCount(); i++) {
//            String columnName = myDb.getFromDB("HISTORYDRILL").getColumnName(i);
            List<String> listOfAllCellsInColumnDrill = getColumnFromDB("HISTORYDRILL", entryName);


            for (int j = 0; j < listOfAllCellsInColumnDrill.size(); j++) {
                //Toast.makeText(getBaseContext(), listOfAllCellsInDB.get(j), Toast.LENGTH_LONG).show();

                //here we check to make sure that a line has at least 1 number or 1 letter before even trying to
                //      add it as a siteswap, it prevents crashes
                if (listOfAllCellsInColumnDrill.get(j) != null && !listOfAllCellsInColumnDrill.get(j).isEmpty()) {

                    //now we split listOfAllCellsInDB.get(j) on the parenthesis and put all the split parts into a list

                    String[] splitListOfAllCellsInDB = listOfAllCellsInColumnDrill.get(j).split("\\)\\(");
                    //we go through each item in the list

                    for (int k = 0; k < splitListOfAllCellsInDB.length; k++) {
                        //we only care about the dates since we are looking for the earliest date(the initial date
                        if (k % 4 == 3) {
                            //if we find a date earlier than the current earliest date that we have, we go ahead and change our current
                            //      earliest date(infoString)
                            if (infoType.equals("initialDate")) {
                                //Log.d("dateX2", infoString);
                                //Log.d("dateY2", splitListOfAllCellsInDB[k].replace(")", ""));
                                if (isDateAfterDateOrEqual(infoString, splitListOfAllCellsInDB[k]) &&
                                        !infoString.equals(splitListOfAllCellsInDB[k])) {
                                    Log.d("chaneToZ", splitListOfAllCellsInDB[k]);
                                    infoString = splitListOfAllCellsInDB[k];
                                }
                            }
                            if (infoType.equals("mostRecentDate")) {
                                //Log.d("dateA2", infoString);
                                //Log.d("dateB2", splitListOfAllCellsInDB[k].replace(")", ""));
                                if (isDateAfterDateOrEqual(splitListOfAllCellsInDB[k], infoString) &&
                                        !infoString.equals(splitListOfAllCellsInDB[k])) {
                                    //Log.d("chaneTo", splitListOfAllCellsInDB[k]);
                                    infoString = splitListOfAllCellsInDB[k];
                                }
                            }
                        }
                    }
                }
            }
        }
//
//                        if (k % 4 == 0) {
//                            splitListOfAllCellsInDB[k] = splitListOfAllCellsInDB[k].replace("(", "");
//
////this length is the number of sets time the length of the sets plus the time spent in failed sets
//                            int drillLength = Integer.parseInt(splitListOfAllCellsInDB[k + 1]) *
//                                    Integer.parseInt(splitListOfAllCellsInDB[k + 2]) +
//                                    Integer.parseInt(splitListOfAllCellsInDB[k].split(",")[1]);
////this is getting the time that the run actually ended, it is its begin time plus its length
//                            String endDateAndTimeOfThisDrill = addTimeToStringDate("SECOND", drillLength, splitListOfAllCellsInDB[k + 3]);
//
//
//                            //this keeps track of the most recent drill run
//                            if (secondsSinceMostRecentRun > timeSince("seconds", endDateAndTimeOfThisDrill)) {
//                                secondsSinceMostRecentRun = timeSince("seconds", endDateAndTimeOfThisDrill);
//                                //Log.d("dateAndTimeOfMost..", endDateAndTimeOfThisDrill);
//                                dateAndTimeOfMostRecentRun = endDateAndTimeOfThisDrill;
//                            }
//                        }
//                    }
//                }
//            }
//        }


        return infoString;
    }

    public void fillListOfReasonsForIntentionalEndFromDB() {

        //first we have to get the array from the String in our database
        //listOfReasonsForIntentionalEnd = listOfEndExplanationsFromEnduranceRuns("IntentionalEnd");

        listOfReasonsForIntentionalEnd.clear();

        listOfReasonsForIntentionalEnd.addAll(removeDuplicatesFromList(listOfEndExplanationsFromEnduranceRuns("intentionalEnd")));


//        Toast.makeText(getBaseContext(), "listOfEndExplanationsFromEnduranceRuns(\"IntentionalEnd\").size() = "+
//                Integer.toString(listOfEndExplanationsFromEnduranceRuns("intentionalEnd").size()), Toast.LENGTH_LONG).show();

//        Toast.makeText(getBaseContext(), "listOfReasonsForIntentionalEnd.size() = " +
//                Integer.toString(listOfReasonsForIntentionalEnd.size()), Toast.LENGTH_LONG).show();
//
//        for (int i = 0; i < listOfReasonsForIntentionalEnd.size(); i++) {
//            Toast.makeText(getBaseContext(), listOfReasonsForIntentionalEnd.get(i), Toast.LENGTH_LONG).show();
//
//        }

    }

    public void fillListOfThoughtsBeforeUnintentionalEndFromDB() {


        //first we have to get the array from the String in our database
        //listOfReasonsForIntentionalEnd = listOfEndExplanationsFromEnduranceRuns("unintentionalEnd");

        listOfThoughtsBeforeUnintentionalEnd.clear();

        listOfThoughtsBeforeUnintentionalEnd.addAll(removeDuplicatesFromList(listOfEndExplanationsFromEnduranceRuns("unintentionalEnd")));

//debugging
//        Toast.makeText(getBaseContext(), "listOfEndExplanationsFromEnduranceRuns(\"unintentionalEnd\").size() = "+
//                Integer.toString(listOfEndExplanationsFromEnduranceRuns("unintentionalEnd").size()), Toast.LENGTH_LONG).show();
//debugging
//            Toast.makeText(getBaseContext(), "listOfThoughtsBeforeUnintentionalEnd.size() = "+
//                    Integer.toString(listOfThoughtsBeforeUnintentionalEnd.size()), Toast.LENGTH_LONG).show();

//        for (int i = 0; i < listOfThoughtsBeforeUnintentionalEnd.size(); i++) {
//            Toast.makeText(getBaseContext(), listOfThoughtsBeforeUnintentionalEnd.get(i), Toast.LENGTH_LONG).show();
//
//        }

    }


    public List<String> removeDuplicatesFromList(List<String> listToRemoveDuplicatesFrom) {

        //go through each string in the array
        for (int j = 0; j < listToRemoveDuplicatesFrom.size(); j++) {

            //we are going to compare each string in the list to each string in the list,
            //      so 1 duplicate for each string is what we would expect if there was no duplicates,
            //      before we go through the list for each string we set the number of duplicates to 0
            int numberOfDuplicates = 0;
            //this is where we go through the list again for each string
            for (int k = 0; k < listToRemoveDuplicatesFrom.size(); k++) {
                //if we find a duplicate..
                if (listToRemoveDuplicatesFrom.get(j).equals(listToRemoveDuplicatesFrom.get(k))) {
                    //..we add 1 to our counter
                    numberOfDuplicates++;
                    //if our counter gets higher than 1
                    if (numberOfDuplicates > 1) {
                        //then we remove the duplicate
                        listToRemoveDuplicatesFrom.remove(k);
                        //the indices of the other numbers in the list get reasigned to make up for the missing
                        //      string, so we need to reduce 'k' by one so that another string doesn't get skipped
                        k--;

                    }
                }
            }
        }

        //this gets rid of any list item that is blank so we are not showing any blank lines in our unintentionalEnddown menu
        for (int l = 0; l < listToRemoveDuplicatesFrom.size(); l++) {
            if (listToRemoveDuplicatesFrom.get(l).equals("")) {
                listToRemoveDuplicatesFrom.remove(l);
                l--;
            }
        }

        return listToRemoveDuplicatesFrom;
    }


    //hitting 'ok' on a blank explanation dialog crashes

    //the second run crashes the app after inputting the explanation,
    //      after that crash, just inputting the pattern in the main ATV crashes the app

    public List<String> listOfEndExplanationsFromEnduranceRuns(String endType) {

        List<String> listToReturn = new ArrayList<>();

        //List<String> listOfAllCellsInDB = myDb.getEveryCellFromTable("HISTORYENDURANCE");
        //List<String> listOfAllCellsInDB =  getAllFromDB("HISTORYENDURANCE");
        //the reason we start at 2 is because 0 is the id and 1 is the 'modifiers column, we just want 2 and beyond
        for (int i = 2; i < myDb.getFromDB("HISTORYENDURANCE").getColumnCount(); i++) {
            String columnName = myDb.getFromDB("HISTORYENDURANCE").getColumnName(i);
            List<String> listOfAllCellsInDB = getColumnFromDB("HISTORYENDURANCE", columnName);


            for (int j = 0; j < listOfAllCellsInDB.size(); j++) {
                //Toast.makeText(getBaseContext(), listOfAllCellsInDB.get(j), Toast.LENGTH_LONG).show();

                //now we split listOfAllCellsInDB.get(j) on the parenthesis and put all the split parts into a list
                if (listOfAllCellsInDB.get(j) != null) {
                    String[] splitListOfAllCellsInDB = listOfAllCellsInDB.get(j).split("\\)\\(");
                    //we go through each item in the list

                    for (int k = 0; k < splitListOfAllCellsInDB.length; k++) {
                        //if we find our desired endtype, then we get the list item prior to it
                        if (splitListOfAllCellsInDB[k].equals(endType)) {
                            //and get whatever is between the ' 's
                            String possiblyUsefulExplanation = "";
                            if (splitListOfAllCellsInDB[k - 1].contains("'")) {
                                possiblyUsefulExplanation = splitListOfAllCellsInDB[k - 1].split("'")[1];
                            }
                            //debugging
                            //Toast.makeText(getBaseContext(), possiblyUsefulExplanation + " added to listToReturn", Toast.LENGTH_LONG).show();

                            listToReturn.add(possiblyUsefulExplanation);
                        }
                    }
                }
            }
        }


        Collections.sort(listToReturn, String.CASE_INSENSITIVE_ORDER);

        return listToReturn;


    }

    public void showReasonForIntentionalEndDialog(Boolean inHeadphoneMode) {
        //Log.d("IntX","1");

        fillListOfReasonsForIntentionalEndFromDB();

        View view = (LayoutInflater.from(MainActivity.this)).inflate(R.layout.reasonforintentionalenddialog, null);

        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(MainActivity.this);
        alertBuilder.setView(view);

        //final TextView reasonForIntentionalEndTV = (TextView) view.findViewById(R.id.reasonForIntentionalEndTV);

        final AutoCompleteTextView reasonForIntentionalEndATV =
                (AutoCompleteTextView) view.findViewById(R.id.reasonForIntentionalEndATV);
        ArrayAdapter<String> reasonForIntentionalEndATVAdapter =
                new ArrayAdapter<>(MainActivity.this, android.R.layout.select_dialog_item, listOfReasonsForIntentionalEnd);
        reasonForIntentionalEndATV.setAdapter(reasonForIntentionalEndATVAdapter);
        reasonForIntentionalEndATV.setThreshold(0);//this is number of letters that must match for autocomplete to suggest a word
        reasonForIntentionalEndATV.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Toast.makeText(getBaseContext(), parent.getItemAtPosition(position) + " selected", Toast.LENGTH_LONG).show();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        reasonForIntentionalEndATV.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    reasonForIntentionalEndATV.showDropDown();
                    hideKeyboard();
                }
            }
        });
        reasonForIntentionalEndATV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reasonForIntentionalEndATV.showDropDown();

            }
        });


        alertBuilder.setCancelable(true)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        reasonForOkButtonClicked(reasonForIntentionalEndATV.getEditableText().toString());

                    }


                });
        alertBuilder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            }
        });
        Dialog dialog = alertBuilder.create();

        dialog.show();


        if (inHeadphoneMode) {
            reasonForIntentionalEndATV.setText("In headphone button mode");
            reasonForOkButtonClicked(reasonForIntentionalEndATV.getEditableText().toString());
            dialog.cancel();
        }
    }



    public void reasonForOkButtonClicked(String lastThoughString){

        //since we made it in here, it means that a run was completed successfully and
        //      so we add an entry to the HISTORYENDURANCE Table
        addRunResultToHistoryEnduranceTableInDB("intentionalEnd", "Ended because '" + lastThoughString + "'.");

        //runsEnduranceTimerCurrentTime = 0;

        //update what is shown in the history tab
        // loadHistoryRecordsFromDB();
    }


    //there is a problem with fillListOfThoughtsBeforeUnintentionalEndFromDB and fillListOfReasonsForIntentionalEndFromDB once there is something in the DB,
    //      both are fine when DB is empty, both are not fine once there is a single cell filled in the DB

    public void showLastThoughtBeforeUnintentionalEndDialog(Boolean inHeadphoneMode) {
        //Log.d("UnintX","1");
        fillListOfThoughtsBeforeUnintentionalEndFromDB();

        View view = (LayoutInflater.from(MainActivity.this)).inflate(R.layout.lastthoughtbeforeunintentionalenddialog, null);

        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(MainActivity.this);
        alertBuilder.setView(view);

        //final TextView reasonForIntentionalEndTV = (TextView) view.findViewById(R.id.reasonForIntentionalEndTV);

        final AutoCompleteTextView lastThoughtBeforeUnintentionalEndATV =
                (AutoCompleteTextView) view.findViewById(R.id.lastThoughtBeforeUnintentionalEndATV);
        ArrayAdapter<String> lastThoughtBeforeUnintentionalEndAdapter =
                new ArrayAdapter<>(MainActivity.this, android.R.layout.select_dialog_item, listOfThoughtsBeforeUnintentionalEnd);
        lastThoughtBeforeUnintentionalEndATV.setAdapter(lastThoughtBeforeUnintentionalEndAdapter);
        lastThoughtBeforeUnintentionalEndATV.setThreshold(0);//this is number of letters that must match for autocomplete to suggest a word
        lastThoughtBeforeUnintentionalEndATV.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Toast.makeText(getBaseContext(), parent.getItemAtPosition(position) + " selected", Toast.LENGTH_LONG).show();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        lastThoughtBeforeUnintentionalEndATV.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    lastThoughtBeforeUnintentionalEndATV.showDropDown();
                    hideKeyboard();
                }
            }
        });
        lastThoughtBeforeUnintentionalEndATV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lastThoughtBeforeUnintentionalEndATV.showDropDown();

            }
        });


        alertBuilder.setCancelable(true)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        lastThoughtOkButtonClicked(lastThoughtBeforeUnintentionalEndATV.getEditableText().toString());




                    }


                });
        alertBuilder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            }
        });
        Dialog dialog = alertBuilder.create();

        dialog.show();

        if(inHeadphoneMode){
            lastThoughtBeforeUnintentionalEndATV.setText("In headphone button mode");
            lastThoughtOkButtonClicked(lastThoughtBeforeUnintentionalEndATV.getEditableText().toString());
            dialog.cancel();
       }
    }

    public void lastThoughtOkButtonClicked(String lastThoughString){


        //since we made it in here, it means that a drill was completed successfully and
        //      so we add an entry to the HISTORYENDURANCE Table
        addRunResultToHistoryEnduranceTableInDB("unintentionalEnd", "Final thought was '" + lastThoughString + "'.");


        //update what is shown in the history tab
        // loadHistoryRecordsFromDB();
    }

    //NEED TO CHECK AND SEE THAT THIS HANDLES 0s CORRECTLY

    public Boolean isValidInput(String stringToCheck) {

        boolean isValid = true;

        //but if the dialog is submited with the name empty, no pattern is added
        if (stringToCheck.equals("")) {
            Toast.makeText(MainActivity.this, "Input is blank.", Toast.LENGTH_LONG).show();
            isValid = false;
        }


        return isValid;
    }

    public int numberOfObjectsInSiteswap(String siteswap) {

        int numberOfObjects = 0;
        int digitSum = 0;
        int digitCount = 0;
        String[] chars = new String[siteswap.length()];

        //debugging
        //Toast.makeText(MainActivity.this, "chars.length =  "+Integer.toString(chars.length), Toast.LENGTH_LONG).show();


        for (int i = 0; i < chars.length; i++) {

            chars[i] = String.valueOf(siteswap.charAt(i));

            //chars[i] = String.valueOf(siteswap.substring(i,i+1));

            //debugging
            //Toast.makeText(MainActivity.this, "chars[i] =  "+chars[i], Toast.LENGTH_LONG).show();

            //first we check to see if the char is a letter, if it is, then we change it it's numerical equivalent
            if (chars[i].equals("a") || chars[i].equals("A")) {
                chars[i] = "10";
            }
            if (chars[i].equals("b") || chars[i].equals("B")) {
                chars[i] = "11";
            }
            if (chars[i].equals("c") || chars[i].equals("C")) {
                chars[i] = "12";
            }
            if (chars[i].equals("d") || chars[i].equals("D")) {
                chars[i] = "13";
            }
            if (chars[i].equals("e") || chars[i].equals("E")) {
                chars[i] = "14";
            }
            if (chars[i].equals("f") || chars[i].equals("F")) {
                chars[i] = "15";
            }
            if (chars[i].equals("g") || chars[i].equals("G")) {
                chars[i] = "16";
            }
            if (chars[i].equals("h") || chars[i].equals("H")) {
                chars[i] = "17";
            }
            if (chars[i].equals("i") || chars[i].equals("I")) {
                chars[i] = "18";
            }
            if (chars[i].equals("j") || chars[i].equals("J")) {
                chars[i] = "19";
            }
            if (chars[i].equals("k") || chars[i].equals("K")) {
                chars[i] = "20";
            }
            if (chars[i].equals("l") || chars[i].equals("L")) {
                chars[i] = "21";
            }
            if (chars[i].equals("m") || chars[i].equals("M")) {
                chars[i] = "22";
            }
            if (chars[i].equals("n") || chars[i].equals("N")) {
                chars[i] = "23";
            }
            if (chars[i].equals("o") || chars[i].equals("O")) {
                chars[i] = "24";
            }
            if (chars[i].equals("p") || chars[i].equals("P")) {
                chars[i] = "25";
            }
            if (chars[i].equals("q") || chars[i].equals("Q")) {
                chars[i] = "26";
            }
            if (chars[i].equals("r") || chars[i].equals("R")) {
                chars[i] = "27";
            }

            //debugging
            // Toast.makeText(MainActivity.this, chars[i], Toast.LENGTH_LONG).show();


            //now we check to see if the char is an int
            if (TextUtils.isDigitsOnly(chars[i])) {
                digitCount++;
                digitSum = digitSum + Integer.parseInt(chars[i].replaceAll("[^0-9]", ""));
            }

            //if there is a "[", then we subtract 1 from the number of digits because there is a multiplex throw
            if (chars[i].equals("[")) {
                digitCount--;
            }

            //there is no need to take any sync stuff into account, so we don't need to check for ( ) , * , or x

        }

        //now that we have gone through every character and gotten a digit sum and a digit count,
        //  we can take the modulus to find out if it is a valid siteswap

        //debugging
        //Toast.makeText(MainActivity.this, "digitSum =  "+Integer.toString(digitSum), Toast.LENGTH_LONG).show();

        //debugging
        //Toast.makeText(MainActivity.this, "digitCount =  "+Integer.toString(digitCount), Toast.LENGTH_LONG).show();
        if (digitSum % digitCount == 0) {
            //if the mod is 0, then it is valid
            numberOfObjects = digitSum / digitCount;
        }

//if 0 gets returned, then the siteswap was not valid
        return numberOfObjects;
    }
    public void fillHistoryRecordsTextView() {
        String table = "";
        List<String> listOfAllHistoryItemsDetails = new ArrayList<>();
        List<Date> listOfAllHistoryItemsDates = new ArrayList<>();
        int indexOfFirstDrillItem = 0;
        String textToSetAsHistoryTextView = "\n";
        String sessionEndDateText = "";
        SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yy HH:mm:ss");

        //this makes an alternating list of all session date/times going beginning,ending,beginning,ending... this will be used later
        //      for marking the sessions in between the history item details
        List<String> listOfAllSessionDates = new ArrayList<>();
        int listOfAllSessionDatesCurrentIndex = 0;

        for (int i = 0; i < histSessionDateAndTimesSpinner.getCount(); i++) {
            listOfAllSessionDates.add(histSessionDateAndTimesSpinner.getItemAtPosition(i).toString().split(" - ")[0]);
            listOfAllSessionDates.add(histSessionDateAndTimesSpinner.getItemAtPosition(i).toString().split(" - ")[1]);

        }


//we are going to cycle through this twice, once for the endurance table, and once for the drill table
        for (int t = 0; t < 2; t++) {
            if (t == 0) {
                table = "HISTORYENDURANCE";
            } else {
                table = "HISTORYDRILL";
            }
            //this cycles through every column of of current history table, it contains the information on every run
            //  that has happened. We skip the 1st column because it is the names of the different combinations of modifiers.
            for (int i = 2; i < myDb.getFromDB(table).getColumnCount(); i++) {
                //Log.d("fillHistoryRec..", "1");
                //since we are going through the table 1 column at a time, we want to get the column name of the column that we are
                //  currently dealing with
                String columnName = myDb.getFromDB(table).getColumnName(i);


                //now we use that column name to fill up this list with each cell of the column. 1 cell = 1 item in the list. inside of a cell
                //  is all the information for each modifier/pattern combination. the format for endurance entries is:
                // (end explanation)(intentionalEnd/unintentionalEnd)(run length)(date/time), as many entries as needed are held in a single cell, the format just
                //  repeats over and over.
                List<String> listOfAllCellsInTheColumn = getColumnFromDB(table, columnName);


                //now we cycle through each of the list items(the cells in our column), each one of these represents a possible different unique
                //  combination of modifiers & pattern. If it contains anything, then we know it is indeed a unique combination, once we know
                //  that, we want to find out if it contains a run that ended in a intentionalEnd and/or unintentionalEnd
                for (int j = 0; j < listOfAllCellsInTheColumn.size(); j++) {
                    //Toast.makeText(getBaseContext(), listOfAllCellsInDB.get(j), Toast.LENGTH_LONG).show();
                    //Log.d("fillHistoryRec..", "2");

                    //here we check to make sure that the list item(cell) is not null or empty, it has to have something in it or we don't
                    //  want anything to do with it
                    if (listOfAllCellsInTheColumn.get(j) != null && !listOfAllCellsInTheColumn.get(j).isEmpty()) {

                        //Log.d("fillHistoryRec..", "3");
                        //now we split the item(cell) on the parenthesis and put all the split parts into a list
                        String[] splitListOfAllCellsInDB = listOfAllCellsInTheColumn.get(j).split("\\)\\(");

                        //now we want to add the first 4 split items, as well as the modifier in the row we are working with
                        //      as well as the entry name of the column we are working with, we add all that to a cell,
                        //      formatted and ready to go, and we also add the date to the 2nd D
                        String modifiers = getColumnFromDB("HISTORYDRILL", "MODIFIERS").get(j);

                        if (modifiers.isEmpty()) {
                            modifiers = "No modifiers";
                        }
                        //the details are organized differently for endur and drill so first we do the endur format
                        if (table.equals("HISTORYENDURANCE")) {
                            //Log.d("fillHistoryRec..", "5");
                            for (int k = 0; k < splitListOfAllCellsInDB.length; k++) {
                                if (k % 4 == 0) {
                                    String endString = "Ended unintentionally.";
                                    if (splitListOfAllCellsInDB[k + 1].equals("intentionalEnd")){
                                        endString = "Ended intentionally.";
                                    }
                                    listOfAllHistoryItemsDetails.add(
                                            splitListOfAllCellsInDB[k + 3].replace(")", "") + "`\n" +
                                                    "Name: " + columnName.split("@@##&&")[1] + "`\n" +
                                                    "Num of objs: " + columnName.split("@@##&&")[2] + "`\n" +
                                                    "Obj type: " + columnName.split("@@##&&")[3] + "`\n" +
                                                    "Modifiers: " + modifiers + "`\n" +
                                                    "Endurance" + initialRunIndicator("HISTORYENDURANCE", modifiers, columnName, splitListOfAllCellsInDB[k + 3].replace(")", "")) + "`\n" +
                                                    splitListOfAllCellsInDB[k].replace("(", "") + "`\n" +
                                                    endString + "`\n" +
                                                    "Length: " + formattedTime(Integer.parseInt(splitListOfAllCellsInDB[k + 2])) +
                                                    personalBestIndicator(modifiers, columnName, splitListOfAllCellsInDB[k + 3].replace(")", "")) + "\n__________\n");
                                    try {
                                        listOfAllHistoryItemsDates.add(formatter.parse(splitListOfAllCellsInDB[k + 3].replace(")", "")));

                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }

                                }
                            }
                        } else {//and here we do the drill format (MAKE SURE TO ADJUST WHAT THE Ks ARE AFTER COPYING
                            for (int k = 0; k < splitListOfAllCellsInDB.length; k++) {
                                if (k % 4 == 0) {
                                    //Log.d("fillHistoryRec..", "6");
                                    splitListOfAllCellsInDB[k] = splitListOfAllCellsInDB[k].replace("(", "");
                                    String drillType = "";
                                    int drillLength = 0;
                                    //if it is a 0, then there will be no comma and we are dealing with a 'dont restart set on unintentionalEnds'
                                    if (splitListOfAllCellsInDB[k].equals("0")) {
                                        drillType = "Don't restart set on unintentionalEnds\n" +
                                                "Sets: " + Integer.parseInt(splitListOfAllCellsInDB[k + 1]);
                                        drillLength = Integer.parseInt(splitListOfAllCellsInDB[k + 1]) *
                                                Integer.parseInt(splitListOfAllCellsInDB[k + 2]);
                                    } else {
                                        drillType = "Restart set on unintentionalEnds\n" +
                                                "Sets: " + Integer.parseInt(splitListOfAllCellsInDB[k + 1]) + "\n" +
                                                "Attempts: " + splitListOfAllCellsInDB[k].split(",")[0];
                                        //if the number of attempts is the same as the number sets, then we know that it was a dropless drill
                                        if (splitListOfAllCellsInDB[k].split(",")[0] == splitListOfAllCellsInDB[k + 1]) {
                                            drillType = drillType + " (dropless)";

                                        }
                                        drillLength = Integer.parseInt(splitListOfAllCellsInDB[k + 1]) *
                                                Integer.parseInt(splitListOfAllCellsInDB[k + 2]) +
                                                Integer.parseInt(splitListOfAllCellsInDB[k].split(",")[1]);
                                    }

                                    listOfAllHistoryItemsDetails.add(
                                            splitListOfAllCellsInDB[k + 3].replace(")", "") + "`\n" + //date/time
                                                    "Name: " + columnName.split("@@##&&")[1] + "`\n" +                 //pattern
                                                    "Num of objs: " + columnName.split("@@##&&")[2] + "`\n" + //# of objects
                                                    "Obj type: " + columnName.split("@@##&&")[3] + "`\n" +    //object type
                                                    "Modifiers: " + modifiers + "`\n" +                                     //modifiers
                                                    "Drill" + initialRunIndicator("HISTORYDRILL", modifiers, columnName, splitListOfAllCellsInDB[k + 3].replace(")", "")) + "`\n" +
                                                    drillType + "`\n" +                                     //either restart or don't
                                                    "Length: " + formattedTime(drillLength) + "\n__________\n");

                                    try {
                                        listOfAllHistoryItemsDates.add(formatter.parse(splitListOfAllCellsInDB[k + 3].replace(")", "")));

                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }

                    }
                }
            }


        }
        //Log.d("fillHistoryRec..", "7");

        //now we have 2 lists,
        //     listOfAllHistoryItemsDetails  is the details we wish to present to the user for each run,
        //     listOfAllHistoryItemsDates is the dates of each of those details, this will be used for deciding which to check next


        for (int i = 0; i < listOfAllHistoryItemsDates.size(); i++) {
            Date earliestDate;
            Date dateOfHistoryItemBeingAdded;
            String dateOfHistoryItemBeingAddedString= "04/22/34 04:28:01";

//            //IM NOT SURE, BUT IT LOOKS LIKE THIS IS NOT USED, MAYBE THIS TRY/CATCH CAN BE DELETED
//            try {
//                earliestDate = formatter.parse("04/22/35 04:28:01");
//
//
//            } catch (ParseException e) {
//                e.printStackTrace();
//            }

//        try {
//            earliestDate=formatter.parse("04/22/35 04:28:01");
//
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }



            if (listOfAllHistoryItemsDates.size() > 0) {
                earliestDate = Collections.min(listOfAllHistoryItemsDates);
                //Log.d("fillHistoryRec..", "8");
                int indexOfEarliestDate = listOfAllHistoryItemsDates.indexOf(earliestDate);
                //if the earliest date in out list is after our date in the far future (way past the death of this code),
                // then we know that we have a date that we want to use
                //if (isDateAfterDateOrEqual("04/21/35 04:28:01", "03/06/17 09:54:01")) {
//                if (isDateAfterDateOrEqual("04/21/35 04:28:01", "03/06/17 09:54:01")) {
//                    //Log.d("earliestDate", "isAfterEasy");
//                }
                //Log.d("earliestDate", formatter.format(earliestDate));
                if (isDateAfterDateOrEqual("04/21/35 04:28:01", formatter.format(earliestDate))) {
                    //Log.d("earliestDate", "isAfter");
                    //          -once we know that it has not been used before, we want to access its details,
                    //                      when we are done with it, we want to change it's date to the distant future
                    //                              (so we won't pull it up again) and we can just keep taking the earliest date
                    try {
                        //Log.d("earliestDate", "in the try");
                        //since we are changing the date in the list, we want to save what it is so we can use it below when we are
                        //  checking to see if we need to show a session date
                        //dateOfHistoryItemBeingAdded = listOfAllHistoryItemsDates.get(indexOfEarliestDate);
                        dateOfHistoryItemBeingAddedString = formatter.format(listOfAllHistoryItemsDates.get(indexOfEarliestDate));
                        //now we can change the date in the list to a distant future date
                        listOfAllHistoryItemsDates.set(indexOfEarliestDate, formatter.parse("04/22/35 04:28:01"));

                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    //   -after we changed the date, we use the 'indexOfEarliestDate' to access it's details from 'listOfAllHistoryItemsDetails'

                    //   -we check to see if it makes it through the filter
                    //          (use the hints below to make the filters of each different kind)

                    //this is going to be a main filter method, inside there should be a different filter method for
                    if (passesFilter(listOfAllHistoryItemsDetails.get(indexOfEarliestDate))) {
                        ////Log.d("textToSet1...", textToSetAsHistoryTextView);

                        //before we add any history item to the text that is going to be displayed,
                        //  we want to check to see if a session is either beginning or ending
                        //      -we only need to do this if the session checkbox filter is not checked
                        sessionEndDateText = "";
                        //now we want to check to see if there is a session begin or end date/time that should be shown.
                        //  we only want to show one if the following 3 conditions are met
                        //  1) we are not currently filtering by session(because if we are then that would mean that only
                        //          one session is being shown anyways.
                        //  2) our list of sessions date/times is not empty
                        //  3) the index of the next session date/time to look at is not greater than or equal to the size of the list,
                        //      this is because we add 1 to the index every time we show a session date and if the index is greater or equal
                        //      to the size of the list then there is no more session dates/times to show
                        if (!histRunsNotInThisSessionCheckbox.isChecked() &&
                                allHistoryCheckboxesAreUnchecked() &&//for now we are only showing sessions if
                                //there are no history checkbxes checked
                                listOfAllSessionDates.size()>0 &&
                                listOfAllSessionDates.size()>listOfAllSessionDatesCurrentIndex) {
                            //now we check to see if the date of the history item that we are about to add to the textview
                            // is after or the same as the date of the next session to be shown
                            if (isDateAfterDateOrEqual(dateOfHistoryItemBeingAddedString,
                                    listOfAllSessionDates.get(listOfAllSessionDatesCurrentIndex))) {
                                //if that is the case then we go ahead and save that session date because we are going to use it later
                                String sessionDateForTextView = listOfAllSessionDates.get(listOfAllSessionDatesCurrentIndex);
                                if (listOfAllSessionDatesCurrentIndex % 2 == 0) { //if the current index is either 0 or even,
                                    //  then we know that it is a begin date
                                    sessionDateForTextView = " ***Session began***\n"
                                            + sessionDateForTextView + "\n";

                                    //and now we add the session string that we made above to the textview that is going to be shown
                                    //textToSetAsHistoryTextView = textToSetAsHistoryTextView + sessionDateForTextView+"\n";
                                    textToSetAsHistoryTextView = sessionDateForTextView + "\n" + textToSetAsHistoryTextView;

                                    //we add 1 to this since we are using the current index to keep track of which
                                    //  of our dates want to check next
                                    listOfAllSessionDatesCurrentIndex++;
                                } else {//otherwise we know that it is an end date, we only want to use the end date if
                                    //      it is not equal to the run about to be added
                                    if (!dateOfHistoryItemBeingAddedString.equals
                                            (listOfAllSessionDates.get(listOfAllSessionDatesCurrentIndex))) {
                                        sessionDateForTextView = " ***Session ended***\n" + sessionDateForTextView;
                                        //since it is an end date, we know that we want to immediately show the following
                                        // begin date if there is one.
                                        //First we add 1 to our index number
                                        listOfAllSessionDatesCurrentIndex++;
                                        //Then we make sure that there another session date/time in our list
                                        if (listOfAllSessionDatesCurrentIndex < listOfAllSessionDates.size()) {
                                            //if so, then we add the next begin date/time to the string
//                                        sessionDateForTextView = sessionDateForTextView +
//                                                " ***Session began***\n" +
//                                                listOfAllSessionDates.get(listOfAllSessionDatesCurrentIndex) + "\n";
                                            sessionDateForTextView = " ***Session began***\n" +
                                                    listOfAllSessionDates.get(listOfAllSessionDatesCurrentIndex) + "\n\n" +
                                                    sessionDateForTextView;

                                        }
                                        //and now we add the session string that we made above to the textview that is going to be shown
                                        //textToSetAsHistoryTextView = textToSetAsHistoryTextView + sessionDateForTextView+"\n";
                                        textToSetAsHistoryTextView = sessionDateForTextView + "\n" + textToSetAsHistoryTextView;

                                        //we add 1 to this since we are using the current index to keep track of which
                                        //  of our dates want to check next
                                        listOfAllSessionDatesCurrentIndex++;
                                    }
//                                    else{
//                                        Log.d("firstZ", dateOfHistoryItemBeingAddedString);
//                                        Log.d("firstX", listOfAllSessionDates.get(listOfAllSessionDatesCurrentIndex));
//
//                                    }
                                }


                            }


                        }
                                //once any session dates have been added to the textview, then we add the history item details
                            //   to our string which we will be showing in our textview
//                                textToSetAsHistoryTextView = textToSetAsHistoryTextView +
//                                        listOfAllHistoryItemsDetails.get(indexOfEarliestDate);
                            textToSetAsHistoryTextView = "\n"+listOfAllHistoryItemsDetails.get(indexOfEarliestDate) +
                                    textToSetAsHistoryTextView;



                    }

                }
            }

        }
        //Log.d("textToSet2...", textToSetAsHistoryTextView);


        if(!histRunsNotInThisSessionCheckbox.isChecked() &&//make sure that the session filter is not checked
                listOfAllSessionDates.size() > 0 &&//make sure that the there are session dates
                !runCurrentlyInSession() &&//make sure that we are not currently in a session
                //and make sure that the cooresponding '***session began***' has not been filtered out
                textToSetAsHistoryTextView.contains("***Session began***\n"+listOfAllSessionDates.get(listOfAllSessionDates.size() - 2))){
            textToSetAsHistoryTextView = "\n" + " ***Session ended***\n" +
                    listOfAllSessionDates.get(listOfAllSessionDates.size() - 1) + "\n" +
                    textToSetAsHistoryTextView;
        }if(beingUsedForSummary) {
            historyRecordsTextView.setText(textToSetAsHistoryTextView);
        }else{
            historyRecordsTextView.setText(textToSetAsHistoryTextView.replace("`","").replace("__________",""));
        }
    }





            //              each type
//         histObjectTypesOtherThanCurrentCheckbox; - "Obj type: CURRENT
//     histNumberOfObjectsOtherThanCurrentCheckbox; - "Num of objs: CURRENT
//     histPatternNamesOtherThanCurrentCheckbox; -  I guess just look for pattern name(If i don't like this I could always "Pattern: CURRENT
//     histModifierCombinationOtherThanExactlyCurrentCheckbox; - "Modifiers: CURRENT
//


//     histRunsNotInThisSessionCheckbox; - this can probably be done with just the 'listOfAllHistoryItemsDates' and a before/after check
//        histSessionDateAndTimesSpinner;

//     histEnduranceCheckbox; - use 'indexOfFirstDrillItem'
//     histIntentionalEndCheckbox; - use "\nEnded with a intentionalEnd\n" (see if we can actually use the \n to reduce
            //                                                              mistaking in a comment for and actual unintentionalEnd/intentionalEnd
//     histUnintentionalEndCheckbox; - same as intentionalEnd above
//     histEndurancePersonalBestCheckbox; - IF WE MAKE A (PB) INDICATOR LIKE WE ALREADY HAVE FOR THE OLD METHOD, WE CAN JUST LOOK FOR THAT
//     histEnduranceNotPersonalBestCheckbox; - SAME AS ABOVE
//     histDrillCheckbox;  - use 'indexOfFirstDrillItem'
//     histRestartSetCheckbox; - check for "Drill\nRestart set on unintentionalEnd"
//     histRestartSetDroplessCheckbox; - check for "(dropless)"
//     histRestartSetNotDroplessCheckbox; - check for "(dropless)"
//     histDontRestartCheckbox; - check for "Drill\nDon't restart set on unintentionalEnd"
//     histInitialRunCheckbox; - IF WE MAKE AN INITIAL RUN INDICATOR, PUT IT IN THE DETAILS, WE CAN CHECK FOR THAT, IT WILL
            //                              BE NICE TO HAVE THAT MARKER THERE FOR THE USER AS WELL
//     histNonInitialRunCheckbox; -SAME AS ABOVE
            //
            // of all the modifier & entry combinations, some of these have multiple runs in 1 cell,
            //      the decision to make is whether or not we want each cell of listOfAllHistoryItems to represent 1 run,
            //          or every mod & entry combo.
            //      if we do each one repesents 1 run, then we could use a 2D array and make the 2nd D the date of the run which would
            //          make it easy to find the next chronological item
            //              -to do this, we need to change what is above from filling cells with a bunch of runs to only filling cells
            //                  with 1 run and only doing it if they are not null, and at the same time, get the date from the run
            //                  and stick it in as the 2nd D. WE WANT TO GET MORE CODE FROM THE STATS (//usable)

            //THIS MIGHT BE GARBAGE THAT WE DONT NEED
//
//            //now we cycle through each of the list items(the cells in our column), each one of these represents a possible different unique
//            //  combination of modifiers & pattern. If it contains anything, then we know it is indeed a unique combination, once we know
//            //  that, we want to find out if it contains a run that ended in a intentionalEnd and/or unintentionalEnd
//            for (int j = 0; j < listOfAllCellsInTheColumn.size(); j++) {
//                //Toast.makeText(getBaseContext(), listOfAllCellsInDB.get(j), Toast.LENGTH_LONG).show();
//
//
//                //here we check to make sure that the list item(cell) is not null or empty, it has to have something in it or we don't
//                //  want anything to do with it
//                if (listOfAllCellsInTheColumn.get(j) != null && !listOfAllCellsInTheColumn.get(j).isEmpty()) {
//
//                    //now we split the item(cell) on the parenthesis and put all the split parts into a list
//                    String[] splitListOfAllCellsInDB = listOfAllCellsInTheColumn.get(j).split("\\)\\(");

            //          -this should be similar to stats, except we want to look at every item after each item is checked and always check
            //              the next chronological item in order
            //          -when figuring out which history record to add next, we must be looking at both HISTORY tables since the next record
            //              could be endur or drill
            //          -we don't want to keep loading from our DB so we are going to want to make a list, this list will hold both
            //              endur and drill, and we will keep cycling through it to find the next record to add.
            //              -to make this list we probably want to cycle through every(non-modifier) cell in either table and add each one
            //                  to a list, keep track of the list item id we are on when we finish with the first history table
            //                  and then use that as the cuttoff point to filter out either run type
            //      -check each item against the filter
            //      -add the items that made it past the filter to the textview
            //          -for this we can probably use some code from the session history section

            //      -this function needs to take into account the filter checkboxes that are checked
            //          -every time an record is about to be added to historyRecordsTextView, we need to check to see if
            //              it is a filtered entryType
            //          -we must mantain a 'nextSessionEndsDateAndTime', and every time we go to add a new record, if the
            //              nextSessionEndsDateAndTime is prior to it, then we add the text to indicate that a session ended,
            //              then one began, and then we update our 'nextSessionEndsDateAndTime, and finaly we add the new record to
            //              the textview


    public Boolean allHistoryCheckboxesAreUnchecked(){
        Boolean areUnchecked = true;
        if (histObjectTypesOtherThanCurrentCheckbox.isChecked() ||
        histNumberOfObjectsOtherThanCurrentCheckbox.isChecked() ||
        histPatternNamesOtherThanCurrentCheckbox.isChecked() ||
        histModifierCombinationOtherThanExactlyCurrentCheckbox.isChecked() ||
        histModifierCombinationThatDoesntContainCurrentCheckbox.isChecked() ||
        histRunsNotInThisSessionCheckbox.isChecked() ||
        histIntentionalEndCheckbox.isChecked() ||
        histUnintentionalEndCheckbox.isChecked() ||
        histEndurancePersonalBestCheckbox.isChecked() ||
        histEnduranceNotPersonalBestCheckbox.isChecked() ||
        histRestartSetCheckbox.isChecked() ||
        histRestartSetDroplessCheckbox.isChecked() ||
        histRestartSetNotDroplessCheckbox.isChecked() ||
        histDontRestartCheckbox.isChecked() ||
        histInitialRunCheckbox.isChecked() ||
        histNonInitialRunCheckbox.isChecked()){
            areUnchecked = false;
        };
        return areUnchecked;
    }


    public String personalBestIndicator(String modifiers, String columnName, String dateOfRun ) {
        if (modifiers.equals("No modifiers")){
            modifiers = "";
        }
        //Log.d("Xmodifiers", modifiers);
        //Log.d("XcolumnName", columnName);
        //Log.d("XdateOfRun", dateOfRun);
        String personalBestBrakerIndicator = "(PB)";
        //we use the inputs to determine if this was a personal best brake or not,
        //  we must access the 'HISTORYENDURANCE' table

        List<String> listOfHistoryItems = new ArrayList<>();
        Collections.addAll(listOfHistoryItems, (getCellFromDB("HISTORYENDURANCE", columnName,
                "MODIFIERS", modifiers).split("\\)\\(")));


        int lengthOfThisRun = -1;
        String endTypeOfThisRun = "";



        //first i want to find the personal best of the date that we have passed through, then i can go through and find every other date
        //  and check to see if it is prior to it, if it is then i can check to see if it's length is longer then the length of the
        //  original
        //Log.d("listOfHi.size()", Integer.toString(listOfHistoryItems.size()));
        //we are going to go through all the entries and find the length of the one that we passed through
        for (int i = 0; i < listOfHistoryItems.size(); i++) {
            //Log.d("dateOfRun", dateOfRun);
            //Log.d("listOfHist(i)", listOfHistoryItems.get(i).replaceAll("\\)", ""));
            if (listOfHistoryItems.get(i).replaceAll("\\)", "").equals(dateOfRun)) {
                lengthOfThisRun = Integer.parseInt(listOfHistoryItems.get(i - 1).replaceAll("[^0-9]", ""));
                endTypeOfThisRun = (listOfHistoryItems.get(i - 2));
                //Log.d("lengthOfThisRun", Integer.toString(lengthOfThisRun));
                //Log.d("endTypeOfThisRun", endTypeOfThisRun);
            }
        }

        //now we are going to go through all the entries and find any with dates before the one we passed through and
        //  check to see if they have lengths exceeding the one we found above for 'lengthOfThisRun
        for (int i = 0; i < listOfHistoryItems.size(); i++) {
            //THIS WAS A 1, BUT I THINK IT SHOULD BE 3
            if (i%4==3){//if this is the case, then we know i is a date/time
                //Log.d("otherLength", listOfHistoryItems.get(i - 1).replaceAll("[^0-9]", ""));
                //Log.d("otherType", listOfHistoryItems.get(i - 2));
                if(!listOfHistoryItems.get(i).equals(dateOfRun) && isDateAfterDateOrEqual(dateOfRun,listOfHistoryItems.get(i))){
                    if (Integer.parseInt(listOfHistoryItems.get(i - 1).replaceAll("[^0-9]", ""))>lengthOfThisRun &&
                            //and the endTypes match(wither they are both 'intentionalEnd' or both 'unintentionalEnd'
                            endTypeOfThisRun.equals(listOfHistoryItems.get(i - 2))){

                        personalBestBrakerIndicator = "";
                    }
                }

            }

        }
               return personalBestBrakerIndicator;
    }

    public String initialRunIndicator(String table, String modifiers, String columnName, String dateOfRun ){
        String initialRunIndicator = "(initial)";
        //we use the inputs to determine if this was a personal best brake or not,
        //  we must access the 'HISTORYENDURANCE' table

        if(modifiers.equals("No modifiers")){
            modifiers = "";
        }

        List<String> listOfHistoryItems = new ArrayList<>();
        Collections.addAll(listOfHistoryItems, (getCellFromDB(table, columnName,
                "MODIFIERS", modifiers).split("\\)\\(")));
        //Log.d("initSize", Integer.toString(listOfHistoryItems.size()));
        //Log.d("initListOfH", listOfHistoryItems.get(0));
        //Log.d("Itable", table);
        //Log.d("Imodifiers", modifiers);
        //Log.d("IcolumnName", columnName);
        //Log.d("IdateOfRun", dateOfRun);
        //now we are going to go through all the entries and find any with dates before the one we passed through,
        // //       if we find one, then and
        //  check to see if they have lengths exceeding the one we found above for 'lengthOfThisRun
        for (int i = 0; i < listOfHistoryItems.size(); i++) {
            if (table.equals("HISTORYENDURANCE")) {
                //THIS WAS A 1, BUT I THINK 3 IS CORRECT
                if (i % 4 == 3) {//if this is the case, then we know i is a date/time
                    //Log.d("initListI", listOfHistoryItems.get(i));
                    //Log.d("InitdateOfRun", dateOfRun);
                    if (!listOfHistoryItems.get(i).equals(dateOfRun) && isDateAfterDateOrEqual(dateOfRun, listOfHistoryItems.get(i))) {
                        initialRunIndicator = "";
                        //this is a hacky fix, but for some reason if there is only 1 run, and it is a run that used a modifier,
                        //  then even though it is the only run it does not show it as the initial one, it seems to fix itself once
                        //  there has been another run though. So by checking to see if the size is less than 5 we can see it is the only
                        //  run and call it an initial
                        if(listOfHistoryItems.size()<5){
                            initialRunIndicator = "(initial)";
                        }
                    }

                }

            }
            if (table.equals("HISTORYDRILL")) {
                if (i % 4 == 3) {//if this is the case, then we know i is a date/time
                    if (!listOfHistoryItems.get(i).equals(dateOfRun) && isDateAfterDateOrEqual(dateOfRun, listOfHistoryItems.get(i))) {
                        initialRunIndicator = "";
                        //this is a hacky fix, but for some reason if there is only 1 run, and it is a run that used a modifier,
                        //  then even though it is the only run it does not show it as the initial one, it seems to fix itself once
                        //  there has been another run though. So by checking to see if the size is less than 5 we can see it is the only
                        //  run and call it an initial
                        if(listOfHistoryItems.size()<5){
                            initialRunIndicator = "(initial)";
                        }
                    }

                }

            }
        }
        return initialRunIndicator;
    }

    //HOW THE FILTER LIST WORKS: The user selects the checkboxes of the stuff they want to filter out. So, if we only
    //      want to see history items pertaining to stuff that is currently selected
    //      in the main input then we check everything between the '----'s below
    //----
    //  object types other than (currently selected object type)
    //  number of objects other than (currently selected number of objects)
    //  pattern names other than (currently selected pattern name)
    //  modifier combinations other than (currently selected modifiers)
    //----
    //  runs not in this session: (spinner to select a session) - so if the user checks this, then the runs in the
    //      selected session will be the only ones shown
    //  endurance - if this is checked then intentionalEnd and unintentionalEnd are also checked, and none of them willl be shown
    //      intentionalEnd
    //      unintentionalEnd
    //  drill -if this is checked then 'restart', 'dropless', and 'dont restart' are checked
    //      restart - if this is checked then 'dropless' is checked
    //         dropless
    //         not dropless sessions
    //      don't restart
    //  initial session
    //  non-initial session - if this is the only thing checked, then we have a complete list of initial sessions
    //  personal best brake
    //  non-personal best brakes - if this is checekd then we have a complete list of personal best brake sessions



    public boolean passesFilter(String stringToCheck) {

      boolean passed = false;
     //if our stringToCheck passes all the filters, then we set passed as true, otherwise, it is false
        if(passesObjectTypesOtherThanCurrentFilter(stringToCheck) && //if(stringToCheck.contains("Obj type: CURRENT"){FALSE}
     passesNumberOfObjectsOtherThanCurrentFilter(stringToCheck) &&//"Num of objs: CURRENT
     passesPatternNamesOtherThanCurrentFilter(stringToCheck) &&//I guess just look for pattern name(If i don't like this I could always "Pattern: CURRENT
     passesModifierCombinationOtherThanExactlyCurrentFilter(stringToCheck) &&//"Modifiers: CURRENT
     passesModifierCombinationDoesntContainCurrentFilter(stringToCheck) &&//"Modifiers: CURRENT
//


     passesRunsNotInThisSessionFilter(stringToCheck) &&//this can probably be done with just the 'listOfAllHistoryItemsDates' and a before/after check
        //histSessionDateAndTimesSpinner;

          passesEnduranceFilter(stringToCheck) &&// use 'indexOfFirstDrillItem'
     passesIntentionalEndFilter(stringToCheck) &&// use "\nEnded with a intentionalEnd\n" (see if we can actually use the \n to reduce
     //                                                              mistaking in a comment for and actual unintentionalEnd/intentionalEnd
     passesUnintentionalEndFilter(stringToCheck) &&// same as intentionalEnd above
     passesEndurancePersonalBestFilter(stringToCheck) &&// IF WE MAKE A (PB) INDICATOR LIKE WE ALREADY HAVE FOR THE OLD METHOD, WE CAN JUST LOOK FOR THAT
     passesEnduranceNotPersonalBestFilter(stringToCheck) &&// SAME AS ABOVE
     passesDrillFilter(stringToCheck) &&//  - use 'indexOfFirstDrillItem'
     passesRestartSetFilter(stringToCheck) &&// check for "Drill\nRestart set on unintentionalEnd"
     passesRestartSetDroplessFilter(stringToCheck) &&// check for "(dropless)"
     passesRestartSetNotDroplessFilter(stringToCheck) &&// check for "(dropless)"
     passesDontRestartFilter(stringToCheck) &&// check for "Drill\nDon't restart set on unintentionalEnd"
     passesInitialRunFilter(stringToCheck) &&// IF WE MAKE AN INITIAL RUN INDICATOR, PUT IT IN THE DETAILS, WE CAN CHECK FOR THAT, IT WILL
     //                              BE NICE TO HAVE THAT MARKER THERE FOR THE USER AS WELL
     passesNonInitialRunCheckbox(stringToCheck)){//SAME AS ABOVE

            //if it passes all of the filters, then this goes through as 'true' and we shall show this history items details
         passed = true;
     }
     //Log.d("passesFilter", String.valueOf(passed));
     return passed;
}
    public boolean passesObjectTypesOtherThanCurrentFilter(String stringToCheck){
        //if(stringToCheck.contains("Obj type: CURRENT"){passed = false;}


        boolean passed = true;
        if(histObjectTypesOtherThanCurrentCheckbox.isChecked()) {
            //Log.d("lots", "1");
            //fillObjectTypeATVsfromDB();
            if (!stringToCheck.contains("Obj type: " + objTypeSpinner.getSelectedItem().toString())) {
                passed = false;
            }

        }
        //Log.d("passesObjectTypes..", String.valueOf(passed));
        return passed;
    }

    public boolean passesNumberOfObjectsOtherThanCurrentFilter(String stringToCheck){
        //"Num of objs: CURRENT
         boolean passed = true;
        if(histNumberOfObjectsOtherThanCurrentCheckbox.isChecked()) {


//IF WE END UP WITH AN ISSUE, MAYBE THERE IS SOMETHING IN THIS FUNCTION THAT WE WANT. THE WHOLE FUNCTION IS CAUSING LAG BECAUSE IT
            //      IT KEEPS LOOPING AROUND
           // updateAddPatternNumberOfObjectsFromDB();

            if (!stringToCheck.contains("Num of objs: " + numOfObjsSpinner.getSelectedItem().toString())) {
                passed = false;
            }

        }
        //Log.d("passesNumberOf..", String.valueOf(passed));
        return passed;
    }
    public boolean passesPatternNamesOtherThanCurrentFilter(String stringToCheck){
        //I guess just look for pattern name(If i don't like this I could always "Pattern: CURRENT

        boolean passed = true;
        if(histPatternNamesOtherThanCurrentCheckbox.isChecked()) {



        if(!stringToCheck.contains("Name: "+patternsATV.getEditableText().toString()+"`")){passed = false;}
                         }
        //Log.d("passesPatternName..", String.valueOf(passed));
        return passed;
    }
    public boolean passesModifierCombinationOtherThanExactlyCurrentFilter(String stringToCheck){
        //"Modifiers: CURRENT

        boolean passed = true;
        if(histModifierCombinationOtherThanExactlyCurrentCheckbox.isChecked()) {

            //String thisRunsModifiers[] = stringToCheck.split("Modifiers: ")[1].split("`")[0].split(",");

            //first we make the two lists of modifiers, the ones for the run we are filtering and the ones that are in the main input,
            //      we replace any spaces with not spaces
            List<String> thisRunsModifiersList = new ArrayList<String>(Arrays.asList(stringToCheck.split("Modifiers: ")[1].split("`")[0].split(",")));
           for(int i=0;i<thisRunsModifiersList.size();i++){
               thisRunsModifiersList.set(i,thisRunsModifiersList.get(i).replaceAll(" ", ""));
           }

            List<String> mainInputsModifiersList = new ArrayList<String>(Arrays.asList(modifierMATV.getEditableText().toString().split(",")));
            for(int i=0;i<mainInputsModifiersList.size();i++){
                mainInputsModifiersList.set(i,mainInputsModifiersList.get(i).replaceAll(" ", ""));
            }

//now we alphebetize our lists
            Collections.sort( thisRunsModifiersList );
           Collections.sort( mainInputsModifiersList );

//            if(!isTwoArrayListsWithSameValues(thisRunsModifiersList,mainInputsModifiersList)){
//                passed = false;
//            }
           // Log.d("thisRunsA..", Integer.toString(thisRunsModifiersList.size()));
            //Log.d("mainInpuA..", Integer.toString(mainInputsModifiersList.size()));
           // Log.d("passesModifi..", String.valueOf(passed));
if(thisRunsModifiersList.size() == mainInputsModifiersList.size()){
    for (int i = 0; i<thisRunsModifiersList.size(); i++){
        Log.d("thisRunsB..", thisRunsModifiersList.get(i));
        Log.d("mainInpuB..", mainInputsModifiersList.get(i));
        if (thisRunsModifiersList.get(i).equals("Nomodifiers")){
            thisRunsModifiersList.set(i, "");
        }
        if(!thisRunsModifiersList.get(i).equals(mainInputsModifiersList.get(i))){
            passed = false;
        }
    }
}else{
    passed = false;
}


        //if(!stringToCheck.contains("Modifiers: "+modifierMATV.getEditableText().toString())){passed = false;}
                      }
        //Log.d("passesModifi..", String.valueOf(passed));
        return passed;
    }

    public boolean passesModifierCombinationDoesntContainCurrentFilter(String stringToCheck){
        //"Modifiers: CURRENT

        boolean passed = true;
        if(histModifierCombinationThatDoesntContainCurrentCheckbox.isChecked()) {


            List<String> thisRunsModifiersList = new ArrayList<String>(Arrays.asList(stringToCheck.split("Modifiers: ")[1].split("`")[0].split(",")));
            for(int i=0;i<thisRunsModifiersList.size();i++){
                thisRunsModifiersList.set(i,thisRunsModifiersList.get(i).replaceAll(" ", ""));
            }

            List<String> mainInputsModifiersList = new ArrayList<String>(Arrays.asList(modifierMATV.getEditableText().toString().split(",")));
            for(int i=0;i<mainInputsModifiersList.size();i++){
                mainInputsModifiersList.set(i,mainInputsModifiersList.get(i).replaceAll(" ", ""));
            }




            //String thisRunsModifiers[] = stringToCheck.split("Modifiers: ")[1].split("`")[0].split(",");
            for (int i = 0 ; i<mainInputsModifiersList.size(); i++){
                if(!thisRunsModifiersList.contains(mainInputsModifiersList.get(i))){
                    passed = false;
                }

            }
            if(modifierMATV.getEditableText().toString().equals("")){
                passed = true;
            }
        //if(!stringToCheck.contains("Modifiers: "+modifierMATV.getEditableText().toString())){passed = false;}
                      }
        //Log.d("passesModifi..", String.valueOf(passed));
        return passed;
    }

    public boolean passesRunsNotInThisSessionFilter(String stringToCheck){
        //this can probably be done with just the 'listOfAllHistoryItemsDates' and a before/after check
        //'histSessionDateAndTimesSpinner' is used here as well

        boolean passed = true;
        //there is no need to pay attention to this filter if it is unchecked or if there are no sessions currently completed
        if(histRunsNotInThisSessionCheckbox.isChecked()&&histSessionDateAndTimesSpinner.getCount()>0) {
            //Log.d("passesRunsNo..", "made it into filter");


        String beginTime = histSessionDateAndTimesSpinner.getSelectedItem().toString().split(" - ")[0];
        String endTime = histSessionDateAndTimesSpinner.getSelectedItem().toString().split(" - ")[1];
        String stringToChecksDate = stringToCheck.split("\n")[0];//TODO find out if this /n split is actually doing what we think it should, i dont think you can split like that
        if (!(isDateAfterDateOrEqual(stringToChecksDate, beginTime) &&
                isDateAfterDateOrEqual(endTime, stringToChecksDate))) {passed = false;}
                    }
        //Log.d("passesRunsNo..", String.valueOf(passed));
        return passed;
    }
    public boolean passesEnduranceFilter(String stringToCheck){
// use 'indexOfFirstDrillItem'

        boolean passed = true;
        if(histEnduranceCheckbox.isChecked()) {



        if(stringToCheck.contains("\nEndurance\n")){passed = false;}

                    }
        //Log.d("passesEndur..", String.valueOf(passed));
        return passed;
    }
    public boolean passesIntentionalEndFilter(String stringToCheck){
// use "\nEnded with a intentionalEnd.\n" (see if we can actually use the \n to reduce
        //                                                              mistaking in a comment for and actual unintentionalEnd/intentionalEnd

        boolean passed = true;
        if(histIntentionalEndCheckbox.isChecked()) {



        if(stringToCheck.contains("\nEnded with a intentionalEnd\n")){passed = false;}
                   }
        //Log.d("psIntEndFilt", String.valueOf(passed));
        return passed;
    }
    public boolean passesUnintentionalEndFilter(String stringToCheck){
// same as intentionalEnd above
 boolean passed = true;
        if(histUnintentionalEndCheckbox.isChecked()) {



        if(stringToCheck.contains("\nEnded with a unintentionalEnd\n")){passed = false;}
                  }
        //Log.d("psUnIntEndFilt", String.valueOf(passed));
        return passed;
    }

    public boolean passesEndurancePersonalBestFilter(String stringToCheck){
//if the details do contain the (PB) indicator, then it does not make it through the filter
        boolean passed = true;
        if(histEndurancePersonalBestCheckbox.isChecked()) {


        if(stringToCheck.contains("(PB)")){passed = false;}
                   }
        //Log.d("passesEndu..", String.valueOf(passed));
    return passed;
    }

    public boolean passesEnduranceNotPersonalBestFilter(String stringToCheck){
//if the details do not contain the (PB) indicator, then it does not make it through the filter

        boolean passed = true;
        if(histEnduranceNotPersonalBestCheckbox.isChecked()) {


        if(!stringToCheck.contains("(PB)")){passed = false;}
            if(stringToCheck.contains("Sets:")){passed = true;}//we do this because if it has 'Sets:' in it then it is a drill,
            //                                                      so we do not want to filter it for not being a PB
                   }


        //Log.d("passesEnduran..", String.valueOf(passed));
        return passed;
    }
    public boolean passesDrillFilter(String stringToCheck){
//  - use 'indexOfFirstDrillItem'

        boolean passed = true;
        if(histDrillCheckbox.isChecked()) {


        if(stringToCheck.contains("\nDrill\n")){passed = false;}

                  }
        //Log.d("passesDrillFilter", String.valueOf(passed));
        return passed;
    }
    public boolean passesRestartSetFilter(String stringToCheck){
// check for "Drill\nRestart set on unintentionalEnd"

        boolean passed = true;
        if(histRestartSetCheckbox.isChecked()) {


        if(stringToCheck.contains("\nDrill\nRestart set on unintentionalEnd")){passed = false;}
                 }
        //Log.d("passesRestartSetFilter", String.valueOf(passed));
        return passed;
    }
    public boolean passesRestartSetDroplessFilter(String stringToCheck){
//if it is dropless, then we don't show it
        //if it does not contain 'dropless', then we dont show it

        boolean passed = true;
        if(histRestartSetDroplessCheckbox.isChecked()) {

        if(stringToCheck.contains("(dropless)\n")){passed = false;}
                    }
        //Log.d("passesRes..", String.valueOf(passed));
        return passed;
    }
    public boolean passesRestartSetNotDroplessFilter(String stringToCheck){
//if it is not dropless, then we dont show it

        boolean passed = true;
        if(histRestartSetDroplessCheckbox.isChecked()) {


        if(!stringToCheck.contains("(dropless)\n")){passed = false;}
                  }
        //Log.d("passesRestartS..", String.valueOf(passed));
        return passed;
    }
    public boolean passesDontRestartFilter(String stringToCheck){
// check for "Drill\nDon't restart set on unintentionalEnd"

        boolean passed = true;
        if(histDontRestartCheckbox.isChecked()) {

        if(stringToCheck.contains("\nDrill\nDon't restart set on unintentionalEnd")){passed = false;}

                 }
        //Log.d("passesDontResta..", String.valueOf(passed));
        return passed;
    }

    public boolean passesInitialRunFilter(String stringToCheck){
//if the details do contain the (initial) indicator, then it does not make it through the filter

        boolean passed = true;
        if(histInitialRunCheckbox.isChecked()) {

        if(stringToCheck.contains("(initial)")){passed = false;}
                  }
        //Log.d("passesInitia..", String.valueOf(passed));
        return passed;
    }

    public boolean passesNonInitialRunCheckbox(String stringToCheck){
//if the details do not contain the (initial) indicator, then it does not make it through the filter

        boolean passed = true;
        if(histNonInitialRunCheckbox.isChecked()) {

            if(!stringToCheck.contains("(initial)")){passed = false;}
                     }
        //Log.d("passesNonIniti..", String.valueOf(passed));
        return passed;
    }







    public void histFilterExpandCollapseClicked(){

        if (histFilterExpandCollapseTextView.getText().toString().contains("show")){
            histFilterExpandCollapseTextView.setText("(hide filter)");
            historySetFilterVisibility(View.VISIBLE);



        }else{
            histFilterExpandCollapseTextView.setText("(show filter)");
            historySetFilterVisibility(View.GONE);

        }

    }
    public void historySetFilterVisibility(int visibility){

        histMainInputsCheckbox.setVisibility(visibility);
        histObjectTypesOtherThanCurrentCheckbox.setVisibility(visibility);
        histNumberOfObjectsOtherThanCurrentCheckbox.setVisibility(visibility);
        histPatternNamesOtherThanCurrentCheckbox.setVisibility(visibility);
        histModifierCombinationOtherThanExactlyCurrentCheckbox.setVisibility(visibility);
        histModifierCombinationThatDoesntContainCurrentCheckbox.setVisibility(visibility);
        histRunsNotInThisSessionCheckbox.setVisibility(visibility);
        histSessionDateAndTimesSpinner.setVisibility(visibility);
        histEnduranceCheckbox.setVisibility(visibility);
        histIntentionalEndCheckbox.setVisibility(visibility);
        histUnintentionalEndCheckbox.setVisibility(visibility);
        histEndurancePersonalBestCheckbox.setVisibility(visibility);
        histEnduranceNotPersonalBestCheckbox.setVisibility(visibility);
        histDrillCheckbox.setVisibility(visibility);
        histRestartSetCheckbox.setVisibility(visibility);
        histRestartSetDroplessCheckbox.setVisibility(visibility);
        histRestartSetNotDroplessCheckbox.setVisibility(visibility);
        histDontRestartCheckbox.setVisibility(visibility);
        histInitialRunCheckbox.setVisibility(visibility);
        histNonInitialRunCheckbox.setVisibility(visibility);
    }
    public void historySetEditButtonVisibilityIfAllCurrentMainInputFiltersAreChecked(){

        if (histObjectTypesOtherThanCurrentCheckbox.isChecked() &&
        histNumberOfObjectsOtherThanCurrentCheckbox.isChecked() &&
        histPatternNamesOtherThanCurrentCheckbox.isChecked() &&
        histModifierCombinationOtherThanExactlyCurrentCheckbox.isChecked() &&
                histModifierCombinationThatDoesntContainCurrentCheckbox.isChecked()){
            historyEditButton.setVisibility(View.VISIBLE);
        }else{
            historyEditButton.setVisibility(View.GONE);
        }

    }


    public void prepareHistoryTabActivity() {

        histFilterExpandCollapseTextView = (TextView) findViewById(R.id.histFilterExpandCollapseTextView);
        histFilterExpandCollapseTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                histFilterExpandCollapseClicked();
            }
        });
        histFilterExpandCollapseTextView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showInfoDialog("Filters", "This will expand/collapse the list of filter checkboxes.  Checking a filter checkbox will make" +
                        " that type of run not visible in the list of runs.  If no boxes are checked, then all runs will be shown, and" +
                        " if all boxes are checked then no runs will be shown.  The list of runs is always shown with the most recent run" +
                        " at the top. The beginning date/times of all completed sessions are indicated.  In order to edit a run, all of the" +
                        " 'Main Input' checkboxes must be checked.");
                return false;
            }
        });
        histMainInputsCheckbox = (CheckBox) findViewById(R.id.histMainInputsCheckbox);
        histMainInputsCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    histObjectTypesOtherThanCurrentCheckbox.setChecked(true);
                    histNumberOfObjectsOtherThanCurrentCheckbox.setChecked(true);
                    histPatternNamesOtherThanCurrentCheckbox.setChecked(true);
                    histModifierCombinationOtherThanExactlyCurrentCheckbox.setChecked(true);
                    histModifierCombinationThatDoesntContainCurrentCheckbox.setChecked(true);
                }
                fillHistoryRecordsTextView();
            }
        });

        histMainInputsCheckbox.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showInfoDialog("Main input filters", "These four filters are used to filter the history items based on what " +
                        "is currently selected in the 'Main inputs' at the top of the screen. In order to edit the history of " +
                        "a pattern, all four main input filters must be checked.");
                return false;
            }
        });
        histObjectTypesOtherThanCurrentCheckbox = (CheckBox) findViewById(R.id.histObjectTypesOtherThanCurrentCheckbox);
        histObjectTypesOtherThanCurrentCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                historySetEditButtonVisibilityIfAllCurrentMainInputFiltersAreChecked();
                fillHistoryRecordsTextView();
            }
        });
        histObjectTypesOtherThanCurrentCheckbox.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showInfoDialog("Object type filter", "Use this filter to hide all history items that use any object " +
                        "type other than the object type which is currently selected at the top of the screen.");
                return false;
            }
        });
        histNumberOfObjectsOtherThanCurrentCheckbox = (CheckBox) findViewById(R.id.histNumberOfObjectsOtherThanCurrentCheckbox);
        histNumberOfObjectsOtherThanCurrentCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                historySetEditButtonVisibilityIfAllCurrentMainInputFiltersAreChecked();
                fillHistoryRecordsTextView();
            }
        });
        histNumberOfObjectsOtherThanCurrentCheckbox.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showInfoDialog("Number of objects filter", "Use this filter to hide all history items that use any number " +
                        "of objects other than the number of objects which is currently selected at the top of the screen.");
                return false;
            }
        });
        histPatternNamesOtherThanCurrentCheckbox = (CheckBox) findViewById(R.id.histPatternNamesOtherThanCurrentCheckbox);
        histPatternNamesOtherThanCurrentCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                historySetEditButtonVisibilityIfAllCurrentMainInputFiltersAreChecked();
                fillHistoryRecordsTextView();
            }
        });
        histPatternNamesOtherThanCurrentCheckbox.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showInfoDialog("Pattern name filter", "Use this filter to hide all history items that have a pattern name " +
                        "other than the pattern name which is currently selected at the top of the screen.");
                return false;
            }
        });
        histModifierCombinationOtherThanExactlyCurrentCheckbox = (CheckBox) findViewById(R.id.histModifierCombinationOtherThanExactlyCurrentCheckbox);
        histModifierCombinationOtherThanExactlyCurrentCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                historySetEditButtonVisibilityIfAllCurrentMainInputFiltersAreChecked();
                fillHistoryRecordsTextView();
            }
        });
        histModifierCombinationOtherThanExactlyCurrentCheckbox.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showInfoDialog("Exact modifiers filter", "Use this filter to hide all history items that have modifiers " +
                        "other than the exact modifiers which are currently selected at the top of the screen.");
                return false;
            }
        });
        histModifierCombinationThatDoesntContainCurrentCheckbox = (CheckBox) findViewById(R.id.histModifierCombinationThatDoesntContainCurrentCheckbox);
        histModifierCombinationThatDoesntContainCurrentCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                historySetEditButtonVisibilityIfAllCurrentMainInputFiltersAreChecked();
                fillHistoryRecordsTextView();
            }
        });
        histModifierCombinationThatDoesntContainCurrentCheckbox.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showInfoDialog("Doesn't contain modifiers filter", "Use this filter to hide all history items that do not contain " +
                        "the modifiers which are currently selected at the top of the screen.");
                return false;
            }
        });
        histRunsNotInThisSessionCheckbox = (CheckBox) findViewById(R.id.histRunsNotInThisSessionCheckbox);
        histRunsNotInThisSessionCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    historySessionNotesButton.setVisibility(View.VISIBLE);
                }else{
                    historySessionNotesButton.setVisibility(View.GONE);
                }
                fillHistoryRecordsTextView();

            }
        });
        histRunsNotInThisSessionCheckbox.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showInfoDialog("Session filter", "Use this filter to hide all history items which were not in the session " +
                        "selected below.");
                return false;
            }
        });
        histSessionDateAndTimesSpinner = (Spinner) findViewById(R.id.histSessionDateAndTimesSpinner);
        histSessionDateAndTimesSpinner.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showInfoDialog("Session times", "These are the begin & end times of all of your past sessions.  Check the box above and" +
                        " select one to see the runs that were done in that session.");
                return false;
            }
        });
        //I DON'T KNOW WHY, BUT WE SET UP THIS LISTENER ELSEWHERE AS WELL, MAYBE WE SHOULD GET ONE OF THEM... I THINK I TOOK CARE OF IT
        //  BY COMMENTING IT OUT IN THE OTHER PART
        histSessionDateAndTimesSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                histFillSessionRecordsTextViewBasedOnSelectedSession(parent.getItemAtPosition(position).toString());
                fillHistoryRecordsTextView();
                //Log.d("sesSpinner", "item selected");
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        histSessionDateAndTimesSpinner.setVisibility(View.GONE);
        histPopulateSessionDateAndTimesSpinner();

        histEnduranceCheckbox = (CheckBox) findViewById(R.id.histEnduranceCheckbox);
        histEnduranceCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    histIntentionalEndCheckbox.setChecked(true);
                    histUnintentionalEndCheckbox.setChecked(true);
                    histEndurancePersonalBestCheckbox.setChecked(true);
                    histEnduranceNotPersonalBestCheckbox.setChecked(true);
                }
                fillHistoryRecordsTextView();
            }
        });
        histEnduranceCheckbox.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showInfoDialog("Endurance filter", "Check this box to automatically check all four of the endurance" +
                        " related checkboxes below, thus filtering out all endurance runs.");//
                return false;
            }
        });
        histIntentionalEndCheckbox = (CheckBox) findViewById(R.id.histIntentionalEndCheckbox);
        histIntentionalEndCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                fillHistoryRecordsTextView();
            }
        });
        histIntentionalEndCheckbox.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showInfoDialog("Ended intentionally filter", "Checking this box will filter out all of the endurance runs that" +
                        " ended intentionally.");
                return false;
            }
        });
        histUnintentionalEndCheckbox = (CheckBox) findViewById(R.id.histUnintentionalEndCheckbox);
        histUnintentionalEndCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                fillHistoryRecordsTextView();
            }
        });
        histUnintentionalEndCheckbox.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showInfoDialog("Ended unintentionally filter", "Checking this box will filter out all of the endurance runs that" +
                        " ended unintentionally.");
                return false;
            }
        });
        histEndurancePersonalBestCheckbox = (CheckBox) findViewById(R.id.histEndurancePersonalBestCheckbox);
        histEndurancePersonalBestCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                fillHistoryRecordsTextView();
            }
        });
        histEndurancePersonalBestCheckbox.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showInfoDialog("Endurance personal best filter", "Checking this box will filter out any endurance run that " +
                        "ended up breaking the personal best at the time that the run occured.");
                return false;
            }
        });
        histEnduranceNotPersonalBestCheckbox = (CheckBox) findViewById(R.id.histEnduranceNotPersonalBestCheckbox);
        histEnduranceNotPersonalBestCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                fillHistoryRecordsTextView();
            }
        });
        histEnduranceNotPersonalBestCheckbox.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showInfoDialog("Endurance not personal best filter", "Checking this box will filter out any endurance run that " +
                        "didn't end up breaking the personal best at the time that the run occured.");
                return false;
            }
        });
        histDrillCheckbox = (CheckBox) findViewById(R.id.histDrillCheckbox);
        histDrillCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                fillHistoryRecordsTextView();
            }
        });
        histDrillCheckbox.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showInfoDialog("Drill filter", "Check this box to automatically check all four of the drill" +
                        " related checkboxes below, thus filtering out all drill runs.");//
                return false;
            }
        });
        histDrillCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    histRestartSetCheckbox.setChecked(true);
                    histRestartSetDroplessCheckbox.setChecked(true);
                    histRestartSetNotDroplessCheckbox.setChecked(true);
                    histDontRestartCheckbox.setChecked(true);
                }
                fillHistoryRecordsTextView();
            }
        });
        histRestartSetCheckbox = (CheckBox) findViewById(R.id.histRestartSetCheckbox);
        histRestartSetCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    histRestartSetDroplessCheckbox.setChecked(true);
                    histRestartSetNotDroplessCheckbox.setChecked(true);
                }
                fillHistoryRecordsTextView();
            }
        });
        histRestartSetCheckbox.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showInfoDialog("Restart set on drops filter", "Checking this box will automatically check the" +
                        " two checkboxes indented below, 'Dropless' and 'Not dropless', thus filtering out all drill runs that were " +
                        "done in 'Restart set on drops' mode.");
                return false;
            }
        });
        histRestartSetDroplessCheckbox = (CheckBox) findViewById(R.id.histRestartSetDroplessCheckbox);
        histRestartSetDroplessCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                fillHistoryRecordsTextView();
            }
        });
        histRestartSetDroplessCheckbox.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showInfoDialog("Dropless filter", "Checking this box will filter out any drill run that was in " +
                        "'Restart set on drops' mode and no drops occurred.");
                return false;
            }
        });
        histRestartSetNotDroplessCheckbox = (CheckBox) findViewById(R.id.histRestartSetNotDroplessCheckbox);
        histRestartSetNotDroplessCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                fillHistoryRecordsTextView();
            }
        });
        histRestartSetNotDroplessCheckbox.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showInfoDialog("Not dropless filter", "Checking this box will filter out any drill run that was in " +
                        "'Restart set on drops' mode and drops occurred.");
                return false;
            }
        });
        histDontRestartCheckbox = (CheckBox) findViewById(R.id.histDontRestartCheckbox);
        histDontRestartCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                fillHistoryRecordsTextView();
            }
        });
        histRestartSetNotDroplessCheckbox.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showInfoDialog("Don't restart set on drops filter", "Checking this box will filter out all " +
                        " runs that were not in 'Restart set on drops' mode.");
                return false;
            }
        });
        histInitialRunCheckbox = (CheckBox) findViewById(R.id.histInitialRunCheckbox);
        histInitialRunCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                fillHistoryRecordsTextView();
            }
        });
        histInitialRunCheckbox.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showInfoDialog("Initial run filter", "Checking this box will filter out all runs that were not initial " +
                        "runs. An initial run is a run that has specifics which have never been unsed in a run before.");
                return false;
            }
        });
        histNonInitialRunCheckbox = (CheckBox) findViewById(R.id.histNonInitialRunCheckbox);
        histNonInitialRunCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                fillHistoryRecordsTextView();
            }
        });
        histNonInitialRunCheckbox.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showInfoDialog("Non-initial run filter", "Checking this box will filter out all runs that were initial " +
                        "runs. An initial run is a run that has specifics which have never been unsed in a run before.");
                return false;
            }
        });



        historySessionNotesButton = (Button) findViewById(R.id.historySessionNotesButton);
        historySessionNotesButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showInfoDialog("Session history notes", "This button opens a box that allows you to view and edit the notes that are" +
                        " associated with the currently selected session.");
                return true;
            }
        });
        historySessionNotesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //this passes the begintime of the currently selected
                if (histSessionDateAndTimesSpinner.getAdapter().getCount() > 0) {
                    showSessionsNoteDialog(histSessionDateAndTimesSpinner.getSelectedItem().toString().split(" - ")[0]);
                } else {
                    Toast.makeText(MainActivity.this, "There are no sessions.", Toast.LENGTH_LONG).show();


                }

            }
        });
        historySessionNotesButton.setVisibility(View.GONE);

//        historyRadioButtonDrill = (RadioButton) findViewById(R.id.histRadButDrill);
//        historyRadioButtonDrill.setOnLongClickListener(new View.OnLongClickListener() {
//            @Override
//            public boolean onLongClick(View v) {
//                showInfoDialog("History", "This is how you can see the specifics of the past juggling runs completed in the 'JUG' tab. The runs" +
//                        " shown are only of the " +
//                        "currently selected pattern and modifiers in the inputs at the top of the Screen. The type of run is chosen" +
//                        " by selecting 'Endurance' or 'Drill'.");
//                return true;
//            }
//        });
//        historyRadioButtonEndurance = (RadioButton) findViewById(R.id.histRadButEndurance);
//        historyRadioButtonEndurance.setOnLongClickListener(new View.OnLongClickListener() {
//            @Override
//            public boolean onLongClick(View v) {
//                showInfoDialog("History", "This is how you can see the specifics of the past juggling runs completed in the 'JUG' tab. The runs" +
//                        " shown are only of the " +
//                        "currently selected pattern and modifiers in the inputs at the top of the Screen. The type of run is chosen" +
//                        " by selecting 'Endurance' or 'Drill'.");
//                return true;
//            }
//        });
//
//
//        historyPatternTypeRadioGroup = (RadioGroup) findViewById(R.id.histPatternTypeRadioGroup);
//
//        historyPatternTypeRadioGroup.check(histRadButEndurance);
//        historyPatternTypeRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(RadioGroup group, int checkedId) {
//                RadioButton rb = (RadioButton) group.findViewById(checkedId);
//                switch (rb.getId()) {
//                    case R.id.histRadButEndurance:
//                        //Toast.makeText(MainActivity.this, "histEnd", Toast.LENGTH_LONG).show();
//                        loadHistoryRecordsFromDB();
//                        historyIntentionalEndUnintentionalEndBothRadioGroup.setVisibility(View.VISIBLE);
//                        historyDrillRestartSetOnUnintentionalEnds.setVisibility(View.GONE);
//                        break;
//                    case R.id.histRadButDrill:
//                        //Toast.makeText(MainActivity.this, "histDrill", Toast.LENGTH_LONG).show();
//                        loadHistoryRecordsFromDB();
//                        historyIntentionalEndUnintentionalEndBothRadioGroup.setVisibility(View.GONE);
//                        historyDrillRestartSetOnUnintentionalEnds.setVisibility(View.VISIBLE);
//                        break;
//
//                }
//
//            }
//        });
//
//
//        historyRadioButtonIntentionalEnd = (RadioButton) findViewById(R.id.histRadButIntentionalEnd);
//        historyRadioButtonIntentionalEnd.setOnLongClickListener(new View.OnLongClickListener() {
//            @Override
//            public boolean onLongClick(View v) {
//                showInfoDialog("History", "This is used to determine which of the endurance runs to show based on how the run ended, " +
//                        "with a intentionalEnd, a unintentionalEnd, or both.");
//                return true;
//            }
//        });
//        historyRadioButtonUnintentionalEnd = (RadioButton) findViewById(R.id.histRadButUnintentionalEnd);
//        historyRadioButtonUnintentionalEnd.setOnLongClickListener(new View.OnLongClickListener() {
//            @Override
//            public boolean onLongClick(View v) {
//                showInfoDialog("History", "This is used to determine which of the endurance runs to show based on how the run ended, " +
//                        "with a intentionalEnd, a unintentionalEnd, or both.");
//                return true;
//            }
//        });
//        historyRadioButtonBoth = (RadioButton) findViewById(R.id.histRadButBoth);
//        historyRadioButtonBoth.setOnLongClickListener(new View.OnLongClickListener() {
//            @Override
//            public boolean onLongClick(View v) {
//                showInfoDialog("History", "This is used to determine which of the endurance runs to show based on how the run ended, " +
//                        "with a intentionalEnd, a unintentionalEnd, or both.");
//                return true;
//            }
//        });
//
//        historyIntentionalEndUnintentionalEndBothRadioGroup = (RadioGroup) findViewById(R.id.histIntentionalEndUnintentionalEndBothRadioGroup);
//        historyIntentionalEndUnintentionalEndBothRadioGroup.setOnLongClickListener(new View.OnLongClickListener() {
//            @Override
//            public boolean onLongClick(View v) {
//                return true;
//            }
//        });
//        historyIntentionalEndUnintentionalEndBothRadioGroup.check(histRadButBoth);
//        historyIntentionalEndUnintentionalEndBothRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(RadioGroup group, int checkedId) {
//                RadioButton rb = (RadioButton) group.findViewById(checkedId);
//                switch (rb.getId()) {
//                    case R.id.histRadButIntentionalEnd:
//                        //Toast.makeText(MainActivity.this, "histEnd", Toast.LENGTH_LONG).show();
//                        loadHistoryRecordsFromDB();
//                        break;
//                    case R.id.histRadButUnintentionalEnd:
//                        //Toast.makeText(MainActivity.this, "histDrill", Toast.LENGTH_LONG).show();
//                        loadHistoryRecordsFromDB();
//                        break;
//                    case R.id.histRadButBoth:
//                        //Toast.makeText(MainActivity.this, "histDrill", Toast.LENGTH_LONG).show();
//                        loadHistoryRecordsFromDB();
//                        break;
//                }
//
//            }
//        });
//
//        historyDrillRestartSetOnUnintentionalEnds = (CheckBox) findViewById(R.id.histDrillRestartSetOnUnintentionalEnds);
//        historyDrillRestartSetOnUnintentionalEnds.setOnLongClickListener(new View.OnLongClickListener() {
//            @Override
//            public boolean onLongClick(View v) {
//                showInfoDialog("History", "If this checkbox is checked then the drill runs shown will be the drill runs done with the" +
//                        "'Restart set on unintentionalEnds' checkbox checked.");
//                return true;
//            }
//        });
//        historyDrillRestartSetOnUnintentionalEnds.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                //Toast.makeText(MainActivity.this, "histClicked", Toast.LENGTH_LONG).show();
//                loadHistoryRecordsFromDB();
//            }
//        });

        historyEditButton = (Button) findViewById(R.id.historyEditButton);
        historyEditButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showInfoDialog("Edit history", "This button allows you to edit the database entry for this record. Be sure to leave it in the exact " +
                        "format that it is in and only change the details of the entries or it act unexpectedly.\n\nEndurance format:\n" +
                        "(note on end of pattern)(unintentionalEnd or intentionalEnd)(length in seconds)(MM/dd/yy HH:mm:ss of when the run ended)\n\nDrill format:\n" +
                        "(number of attempts)(number of sets)(length of a set in seconds)(MM/dd/yy HH:mm:ss of when the run ended)");

                return true;
            }
        });
        historyEditButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (patternsATV.getText().toString().isEmpty() ||
                        patternsATV.getText().toString().equals("") ||
                        patternsATV.getText().toString() == null) {
                    Toast.makeText(MainActivity.this, "There is no history entry to edit.", Toast.LENGTH_LONG).show();
                } else {

                    new AlertDialog.Builder(MainActivity.this)
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setTitle("Edit History?")
                            .setMessage("Improper format can cause the app to act unexpectedly. If you are sure you want to edit the history," +
                                    " then select the history type to edit, Drill or Endurance:" +
                                    "")
                            .setPositiveButton("Endur", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    if (getCellFromDB("HISTORYENDURANCE", currentEntryName(), "MODIFIERS",
                                            stringOfCurrentlySelectedModifiersInAlphabeticalOrder()) == null ||
                                            getCellFromDB("HISTORYENDURANCE", currentEntryName(), "MODIFIERS",
                                                    stringOfCurrentlySelectedModifiersInAlphabeticalOrder()).equals("")) {

                                        Toast.makeText(MainActivity.this, "There is no endurance history entry to edit.", Toast.LENGTH_LONG).show();

                                    } else {
                                        showHistoryEditDialog("HISTORYENDURANCE");

                                        showInfoDialog("Edit history", "Be sure to use the proper format.\n\nEndurance format:\n(note on end of pattern)" +
                                                "(unintentionalEnd or intentionalEnd)(length in seconds)(MM/dd/yy HH:mm:ss of when the run ended)\n\nDrill format:\n(number of" +
                                                " attempts)(number of sets)(length of a set in seconds)(MM/dd/yy HH:mm:ss of when the run ended)\n\nClick" +
                                                " 'OK' to begin editing.");
                                    }

                                }
                            })
                            .setNeutralButton("Drill", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if (getCellFromDB("HISTORYDRILL", currentEntryName(), "MODIFIERS",
                                            stringOfCurrentlySelectedModifiersInAlphabeticalOrder()) == null ||
                                            getCellFromDB("HISTORYDRILL", currentEntryName(), "MODIFIERS",
                                                    stringOfCurrentlySelectedModifiersInAlphabeticalOrder()).equals("")) {

                                        Toast.makeText(MainActivity.this, "There is no drill history entry to edit.", Toast.LENGTH_LONG).show();

                                    } else {
                                        showHistoryEditDialog("HISTORYDRILL");

                                        showInfoDialog("Edit history", "Be sure to use the proper format.\n\nEndurance format:\n(note on end of pattern)" +
                                                "(unintentionalEnd or intentionalEnd)(length in seconds)(MM/dd/yy HH:mm:ss of when the run ended)\n\nDrill format:\n(number of" +
                                                " attempts)(number of sets)(length of a set in seconds)(MM/dd/yy HH:mm:ss of when the run ended)\n\nClick" +
                                                " 'OK' to begin editing.");
                                    }
                                }
                            })
                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            })
                            .show();

                }



            }
        });

        historyRecordsTextView = (TextView) findViewById(R.id.histRecordsTextView);
        historyRecordsTextView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showInfoDialog("History", "This is the list of all sessions and runs completed in the 'JUG' tab excluding those filtered out. It begins with the most recent " +
                        "and goes in reverse chronological order. Sessions which have not been completed are not shown, so any runs that are in your currnet" +
                        " session will be above the most recent '***Session ended***' indicator.");
                return true;
            }
        });

        //fillHistoryRecordsTextView();

    }

    public void histPopulateSessionDateAndTimesSpinner() {
        listOfSessionDateAndTimes.clear();
        List<String> allBeginTimes = getColumnFromDB("SESSIONS", "BEGINTIME");
        List<String> allEndTimes = getColumnFromDB("SESSIONS", "ENDTIME");
        for (int i = 0; i < allEndTimes.size(); i++) {
            if (allEndTimes.get(i) != null) {
                if (!allEndTimes.get(i).isEmpty()) {
                    listOfSessionDateAndTimes.add(allBeginTimes.get(i) + " - " + allEndTimes.get(i));
                }
            }
        }
        ArrayAdapter<String> histSessionDateAndTimesSpinnerAdapter =
                // new ArrayAdapter<>(MainActivity.this, android.R.layout.select_dialog_item,listOfSessionDateAndTimes);
                new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_spinner_dropdown_item, listOfSessionDateAndTimes);
        histSessionDateAndTimesSpinner.setAdapter(histSessionDateAndTimesSpinnerAdapter);
        histSessionDateAndTimesSpinner.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    hideKeyboard();
                }
            }
        });
        //I DON'T KNOW WHY, BUT WE SET UP THIS LISTENER ELSEWHERE AS WELL, MAYBE WE SHOULD GET ONE OF THEM...
//        histSessionDateAndTimesSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                histFillSessionRecordsTextViewBasedOnSelectedSession(parent.getItemAtPosition(position).toString());
//                fillHistoryRecordsTextView();
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//
//            }
//        });
        //if there is at least 1 session, then we set the selected session as the first session
        if(histSessionDateAndTimesSpinner.getCount()>0) {
            histSessionDateAndTimesSpinner.setSelection(0);
        }
    }
//usable
    public void histFillSessionRecordsTextViewBasedOnSelectedSession(String sessionDateAndTimes) {
        String beginTime = sessionDateAndTimes.split(" - ")[0];
        String endTime = sessionDateAndTimes.split(" - ")[1];

        String textForRecordsTextView = "";

        //Log.d("selected beginTime", beginTime);
        //Log.d("selected endTime", endTime);

        //now we use the begin and end times to get every run between them, we display all the specifics of the runs,
        //  pattern name
        //  modifiers
        //  run type
        //  run details

        //look through every endur run

        //this cycles through every column of of history endurance table, it contains the information on every endurance run
        //  that has happened. We skip the 1st column because it is the names of the different combinations of modifiers.
        for (int i = 2; i < myDb.getFromDB("HISTORYENDURANCE").getColumnCount(); i++) {

            //since we are going through the table 1 column at a time, we want to get the column name of the column that we are
            //  currently dealing with
            String columnName = myDb.getFromDB("HISTORYENDURANCE").getColumnName(i);

            //now we use that column name to fill up this list with each cell of the column. 1 cell = 1 item in the list. inside of a cell
            //  is all the information for each modifier/pattern combination. the format for endurance entries is:
            // (end explanation)(intentionalEnd/unintentionalEnd)(run length)(date/time), as many entries as needed are hed in a single cell, the format just
            //  repeats over and over.
            List<String> listOfAllCellsInTheColumn = getColumnFromDB("HISTORYENDURANCE", columnName);


            //now we cycle through each of the list items(the cells in our column), each one of these represents a possible different unique
            //  combination of modifiers & pattern. If it contains anything, then we know it is indeed a unique combination, once we know
            //  that, we want to find out if it contains a run that ended in a intentionalEnd and/or unintentionalEnd
            for (int j = 0; j < listOfAllCellsInTheColumn.size(); j++) {
                //Toast.makeText(getBaseContext(), listOfAllCellsInDB.get(j), Toast.LENGTH_LONG).show();


                //here we check to make sure that the list item(cell) is not null or empty, it has to have something in it or we don't
                //  want anything to do with it
                if (listOfAllCellsInTheColumn.get(j) != null && !listOfAllCellsInTheColumn.get(j).isEmpty()) {


                    //now we split the item(cell) on the parenthesis and put all the split parts into a list
                    String[] splitListOfAllCellsInDB = listOfAllCellsInTheColumn.get(j).split("\\)\\(");


                    //we go through each item in the list
                    for (int k = 0; k < splitListOfAllCellsInDB.length; k++) {

                        String endType = splitListOfAllCellsInDB[k];

                        //it is set up so that all the stats that are for 'unintentionalEnd' are in 'arrayToReturn' indexes that are 100 higher than
                        //  their 'intentionalEnd' equivalents, so here we check to see which we are dealing with, and set our 'indexAdder'
                        if (endType.equals("intentionalEnd") || endType.equals("unintentionalEnd")) {

                            //once we find a 'intentionalEnd' or a 'unintentionalEnd', we want to find the date/time of the run and see if it falls between
                            //      ourbegin and end date/times

                            if (isDateAfterDateOrEqual(splitListOfAllCellsInDB[k + 2], beginTime) &&
                                    isDateAfterDateOrEqual(endTime, splitListOfAllCellsInDB[k + 2])) {

                                String modifiers = getColumnFromDB("HISTORYENDURANCE", "MODIFIERS").get(j);

                                if (modifiers.isEmpty()) {
                                    modifiers = "No modifiers";
                                }

                                textForRecordsTextView = textForRecordsTextView +
                                        splitListOfAllCellsInDB[k + 2].replace(")", "") + "\n" +
                                        columnName.split("@@##&&")[1] + "\n" +
                                        "Num of objs: " + columnName.split("@@##&&")[2] + "\n" +
                                        "Obj type: " + columnName.split("@@##&&")[3] + "\n" +
                                        "Modifiers: " + modifiers + "\n" +
                                        "Endurance\n*" +
                                        splitListOfAllCellsInDB[k - 1].replace("(", "") + "\n" +
                                        "Ended with a " + splitListOfAllCellsInDB[k] + "\n" +
                                        "Length: " + formattedTime(Integer.parseInt(splitListOfAllCellsInDB[k + 1])) + "\n\n";


                                ////Log.d("we got one", "isDateAfterDateOrEqual");
                            }
                        }
                    }
                }
            }
        }
        //look through every drill run, this is just like above, but for drills instead of endurance
        for (int i = 2; i < myDb.getFromDB("HISTORYDRILL").getColumnCount(); i++) {
            String columnName = myDb.getFromDB("HISTORYDRILL").getColumnName(i);
            List<String> listOfAllCellsInDB = getColumnFromDB("HISTORYDRILL", columnName);

            //now we cycle through each of the list items(the cells in our column), each one of these represents a possible different unique
            //  combination of modifiers & pattern. If it contains anything, then we know it is indeed a unique combination, once we know
            //  that, we want to find out if it contains a run that ended in a intentionalEnd and/or unintentionalEnd
            for (int j = 0; j < listOfAllCellsInDB.size(); j++) {

                //here we check to make sure that a line is not empty, it prevents crashes
                if (listOfAllCellsInDB.get(j) != null && !listOfAllCellsInDB.get(j).isEmpty()) {

                    //now we split listOfAllCellsInDB.get(j) on the parenthesis and put all the split parts into a list

                    String[] splitListOfAllCellsInDB = listOfAllCellsInDB.get(j).split("\\)\\(");
                    //we go through each item in the list

                    for (int k = 0; k < splitListOfAllCellsInDB.length; k++) {


                        if (k % 4 == 0) {

                            if (isDateAfterDateOrEqual(splitListOfAllCellsInDB[k + 3], beginTime) &&
                                    isDateAfterDateOrEqual(endTime, splitListOfAllCellsInDB[k + 3])) {

                                splitListOfAllCellsInDB[k] = splitListOfAllCellsInDB[k].replace("(", "");

                                //it is set up so that all the stats that are for 'restart' are in 'arrayToReturn' indexes that are 100 higher than
                                //  their 'dontRestart' equivalents, so here we check to see which we are dealing with, and set our 'indexAdder'

                                //if this is not a 0 then we know we restart set on unintentionalEnds, it will have 2 values seperated by
                                //  a ',', the value before the comma is the number of attempts, the number after the comma is the amount of seconds spent on failed
                                //  attempts
                                String drillType = "";
                                int drillLength = 0;
                                //if it is a 0, then there will be no comma and we are dealing with a 'dont restart set on unintentionalEnds'
                                if (splitListOfAllCellsInDB[k].equals("0")) {
                                    drillType = "Don't restart set on drops";
                                    drillLength = Integer.parseInt(splitListOfAllCellsInDB[k + 1]) *
                                            Integer.parseInt(splitListOfAllCellsInDB[k + 2]);
                                } else {
                                    drillType = "Restart set on drops\n" +
                                            "Attempts: " + splitListOfAllCellsInDB[k].split(",")[0];
                                    //if the number of attempts is the same as the number sets, then we know that it was a dropless drill
                                    if (splitListOfAllCellsInDB[k].split(",")[0] == splitListOfAllCellsInDB[k + 1]) {
                                        drillType = drillType + " (dropless)";

                                    }
                                    drillLength = Integer.parseInt(splitListOfAllCellsInDB[k + 1]) *
                                            Integer.parseInt(splitListOfAllCellsInDB[k + 2]) +
                                            Integer.parseInt(splitListOfAllCellsInDB[k].split(",")[1]);
                                }
                                String modifiers = getColumnFromDB("HISTORYDRILL", "MODIFIERS").get(j);

                                if (modifiers.isEmpty()) {
                                    modifiers = "No modifiers";
                                }

                                textForRecordsTextView = textForRecordsTextView +
                                        splitListOfAllCellsInDB[k + 3].replace(")", "") + "\n" + //date/time
                                        columnName.split("@@##&&")[1] + "\n" +                 //pattern
                                        "Num of objs: " + columnName.split("@@##&&")[2] + "\n" + //# of objects
                                        "Obj typeI: " + columnName.split("@@##&&")[3] + "\n" +    //object type
                                        "Modifiers: " + modifiers + "\n" +                                     //modifiers
                                        "Drill\n" +
                                        drillType + "\n" +                                     //either restart or don't
                                        "Length: " + formattedTime(drillLength) + "\n\n";


                                //here we keep track of the dates of all the 'don't restart set on unintentionalEnds runs'
                                //(splitListOfAllCellsInDB[k + 3]);

                                //this is the number of sets (splitListOfAllCellsInDB[k + 1]))

                                //this is the length of sets (splitListOfAllCellsInDB[k + 2]))
                            }

                        }
                    }
                }
            }
        }

         //   historySessionRecordsTextView.setText(textForRecordsTextView);

    }

//}if (!textForRecordsTextView.equals("")) {
//        //}if (textForRecordsTextView != null && !textForRecordsTextView.isEmpty(){
////if(!textForRecordsTextView.equals(null) && !textForRecordsTextView.isEmpty()) {
//        historySessionRecordsTextView.setText(textForRecordsTextView);

    public void showHistoryEditDialog(final String table) {

        View view = (LayoutInflater.from(MainActivity.this)).inflate(R.layout.historyeditdialog, null);

        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(MainActivity.this);
        alertBuilder.setView(view);
        final EditText historyEditDialogEditText = (EditText) view.findViewById(R.id.historyEditDialogEditText);
        historyEditDialogEditText.setText(getCellFromDB(table, currentEntryName(), "MODIFIERS",
                stringOfCurrentlySelectedModifiersInAlphabeticalOrder()));

        alertBuilder.setCancelable(true)
                .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        updateDataInDB(table, currentEntryName(), "MODIFIERS",
                                stringOfCurrentlySelectedModifiersInAlphabeticalOrder(), historyEditDialogEditText.getText().toString());
                        //loadHistoryRecordsFromDB();
                        fillHistoryRecordsTextView();//this updates the history textview since we just did something that might
                                                                // change what it should show
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
        Dialog dialog = alertBuilder.create();
        dialog.show();
    }

    public void prepareSettingsTabActivity() {

        TextView maxNumberOfObjectsTV = (TextView) findViewById(R.id.maxNumberOfObjectsTV);
        maxNumberOfObjectsTV.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showInfoDialog("Object max", "This is where you set the max number of objects to be shown in the above list of object numbers.");
                return true;
            }
        });

        settingsMaxNumberOfObjectsEditText = (EditText) findViewById(R.id.settingsMaxNumObjEditText);
        settingsMaxNumberOfObjectsEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {

                    //first we check to see if what was just input into the editText is composed only of digits
                    if (settingsMaxNumberOfObjectsEditText.getText().toString().matches("[0-9]+")) {
                        //now we check to see if it is higher than 200, if it is, then we lower it to 200
                        //so, first we make it into an int
                        int numberInput = Integer.parseInt(settingsMaxNumberOfObjectsEditText.getText().toString().replaceAll("[^0-9]", ""));
                        //this keeps the app from crashing if the max number of objects gets set to less than the
                        //      currently selected object number.
                        if (numberInput < Integer.parseInt(numOfObjsSpinner.getSelectedItem().toString().replaceAll("[^0-9]", ""))) {
                            //just set it to the lowest number of objects, 1
                            numOfObjsSpinner.setSelection(0);
                            myDb.updateData("SETTINGS", "MISC", "ID", "6", "0");

                        }
                        if (numberInput > 200) { //then we check if it is over 200
                            settingsMaxNumberOfObjectsEditText.setText("200"); //if it is, we make it be 200
                        }
                        //this tells exactly where we want to update the max# of objects,
                        //      it goes table, column, id(0 is where we keep this particular add), dataToSend
                        myDb.updateData("SETTINGS", "MISC", "ID", "1", settingsMaxNumberOfObjectsEditText.getText().toString());

                        updateAddPatternNumberOfObjectsFromDB();
                    } else {//if it was not composed only of digits, then we revert to whatever is in the database
                        settingsMaxNumberOfObjectsEditText.setText(getCellFromDB("SETTINGS", "MISC", "ID", "1"));
                    }

                }
            }
        });

        TextView settingsObjTypeLabel = (TextView) findViewById(R.id.settingsObjTypeLabel);
        settingsObjTypeLabel.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showInfoDialog("Prop type", "Use the text box below with the add button to the right to add a new prop type to the list of available" +
                        " prop types above. Use it with the delete button to remove a prop from the list.");
                return true;
            }
        });

        settingsObjectTypeAddButton = (Button) findViewById(R.id.settingsObjTypeAddButton);
        settingsObjectTypeAddButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showInfoDialog("Prop type", "This button adds the text in the below text box as a prop type in the above list of prop types.");
                return true;
            }
        });
        settingsObjectTypeAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                updateDBfromAddInputs();

                View view = (LayoutInflater.from(MainActivity.this)).inflate(R.layout.settypenewdialog, null);

                AlertDialog.Builder alertBuilder = new AlertDialog.Builder(MainActivity.this);
                alertBuilder.setView(view);
                final EditText newSetTypeInputDialogBox = (EditText) view.findViewById(R.id.newSetTypeInputDialogBox);

                alertBuilder.setCancelable(true)
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {


                                //Toast.makeText(MainActivity.this,"clicked", Toast.LENGTH_LONG).show();
                                //this is what is currently in the objectTypeATV, possibly typed by user or selected from autocomplete list by user
                                String currentlyInObjectTypeATV = newSetTypeInputDialogBox.getText().toString();

                                if (isValidInput(currentlyInObjectTypeATV)) {
                                    //Log.d("lots of this", "1");
                                    setObjectTypeLists();

                                    //we want to see if it is already in the autocomplete list or not, if it is not, then we will add it to the database
                                    //String[] fullObjectTypeArray =  getCellFromDB("SETTINGS","MISC","ID","2").split(",");
                                    //Toast.makeText(MainActivity.this, fullObjectTypeArray[0], Toast.LENGTH_LONG).show();
                                    // Toast.makeText(MainActivity.this,"clicked", Toast.LENGTH_LONG).show();


                                    if (containsCaseInsensitive(currentlyInObjectTypeATV, addPatObjTypeForSpinner)) {
                                        //if the object type they input is already in the database
                                        Toast.makeText(MainActivity.this, currentlyInObjectTypeATV + " is already a prop type.", Toast.LENGTH_LONG).show();

                                    } else {


                                        if (newSetTypeInputDialogBox.getText().toString().contains(",")) {
                                            newSetTypeInputDialogBox.setText
                                                    (newSetTypeInputDialogBox.getText().toString().replace(",", ".") + " ");
                                            Toast.makeText(MainActivity.this, "Prop types can't contain the symbol ',' it was replaced with '.'", Toast.LENGTH_LONG).show();

                                        }
                                        //but if it doesn't contain it, we need to make it contain it

                                        //first we make a string out of all the items of our array, each seperated by a comma
                                        //                                                      maybe this needs to be the string[] above
                                        String arraySeperatedByCommas = android.text.TextUtils.join(",", addPatObjTypeForSpinner);

                                        //then we add our new objectType to the end of that string
                                        arraySeperatedByCommas = arraySeperatedByCommas + "," + currentlyInObjectTypeATV;

                                        //then we update our database with our new string
                                        myDb.updateData("SETTINGS", "MISC", "ID", "2", arraySeperatedByCommas);
//HEREEE
                                        //debugging
                                        //Toast.makeText(MainActivity.this, getCellFromDB("SETTINGS", "MISC", "ID", "2") + " isInDB", Toast.LENGTH_LONG).show();


                                        //and we need to update the ATV to make it contain it
                                        //Log.d("lots", "2");
                                        fillObjectTypeATVsfromDB();

                                        Toast.makeText(MainActivity.this, currentlyInObjectTypeATV + " added to prop types.", Toast.LENGTH_LONG).show();


                                        //toast just for debuging
                                        // Toast.makeText(MainActivity.this, "doesn't contain", Toast.LENGTH_LONG).show();
                                    }
                                    //Toast.makeText(MainActivity.this, currentlyInObjectTypeATV, Toast.LENGTH_LONG).show();

                                }
                                //THIS IS JUST FOR DEBUGGING
                                //Toast.makeText(MainActivity.this, newAddModifierInputDialog.getText().toString(), Toast.LENGTH_LONG).show();
                            }
                        });
                Dialog dialog = alertBuilder.create();
                dialog.show();


            }
        });

        settingsObjectTypeRemoveButton = (Button) findViewById(R.id.settingsObjTypeRemoveButton);
        settingsObjectTypeRemoveButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showInfoDialog("Prop type", "This button removes the prop type specified in the below text box from the list of prop types " +
                        "in the above list of prop types.");
                return true;
            }
        });
        settingsObjectTypeRemoveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //showPatterns(); //just used temporarily for debugging, shows the patterns in a message which have input to the db
                //Toast.makeText(MainActivity.this, "Run", Toast.LENGTH_LONG).show();
                View view = (LayoutInflater.from(MainActivity.this)).inflate(R.layout.settypedeletedialog, null);

                AlertDialog.Builder alertBuilder = new AlertDialog.Builder(MainActivity.this);
                alertBuilder.setView(view);

                final Spinner setTypeDeleteDialogBox = (Spinner) view.findViewById(R.id.setTypeDeleteDialogBox);
                ArrayAdapter<String> setTypeDeleteDialogBoxSpinnerAdapter = new ArrayAdapter<>(MainActivity.this,
                        android.R.layout.select_dialog_item, addPatObjTypeForSpinner);
                setTypeDeleteDialogBoxSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                setTypeDeleteDialogBox.setAdapter(setTypeDeleteDialogBoxSpinnerAdapter);
                setTypeDeleteDialogBox.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        // Toast.makeText(getBaseContext(), parent.getItemAtPosition(position) + " selected", Toast.LENGTH_LONG).show();

                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });


                alertBuilder.setCancelable(true)
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //this is what is currently in the objectTypeATV, possibly typed by user or selected from autocomplete list by user
                                String currentlyInObjectTypeATV = setTypeDeleteDialogBox.getSelectedItem().toString();

                                // addPatObjTypeForSpinner.clear();
                                //this is what is in our databse which must also match what is in the autocomplete unintentionalEnddown list for objectTypes
                                // addPatObjTypeForSpinner = Arrays.asList(getCellFromDB("SETTINGS","MISC","id","2").split(","));

                                // String[] bullshit = (getCellFromDB("SETTINGS","MISC","2").split(","));

                                //String[] n = new String[]{"google","microsoft","apple"};
                                List<String> listOfObjectTypes = new ArrayList<>();
                                Collections.addAll(listOfObjectTypes, (getCellFromDB("SETTINGS", "MISC", "ID", "2").split(",")));

                                //now we alphabetize the list
                                Collections.sort(listOfObjectTypes, String.CASE_INSENSITIVE_ORDER);


                                //n = list.toArray(new String[list.size()]);

                                //List<String> shit = Arrays.asList(getCellFromDB("SETTINGS","MISC","ID","2").split(","));


                                //if what is is the database contains the current string we want to remove it from the database
                                if (containsCaseInsensitive(currentlyInObjectTypeATV, listOfObjectTypes)) {


                                    //first we remove the string from the array
                                    listOfObjectTypes.remove(currentlyInObjectTypeATV);

                                    //then we make a string out of all the items of our array, each seperated by a comma
                                    String arraySeperatedByCommas = android.text.TextUtils.join(",", listOfObjectTypes);
//
//                    //then we update our database with our new string
                                    myDb.updateData("SETTINGS", "MISC", "ID", "2", arraySeperatedByCommas);

                                    //and we need to update the ATV to make it contain it
                                    //Log.d("lots", "3");
                                    fillObjectTypeATVsfromDB();

                                    //a toast that tells us exactly what we have done
                                    Toast.makeText(MainActivity.this, currentlyInObjectTypeATV + " removed from prop types.", Toast.LENGTH_LONG).show();

                                    //and then we set the objectType input to blank
                                    //settingsObjectTypeATV.setText("");


                                    //toast just for debuging
                                    // Toast.makeText(MainActivity.this, "doesn't contain", Toast.LENGTH_LONG).show();
                                } else {

                                    Toast.makeText(MainActivity.this, currentlyInObjectTypeATV + " is not a prop type.", Toast.LENGTH_LONG).show();
                                }


                            }

                        });
                Dialog dialog = alertBuilder.create();
                dialog.show();


            }
        });


        settingsRunDrillAudioCheckBox = (CheckBox) findViewById(R.id.settingRunDrillAudioCheckBox);
        settingsRunDrillAudioCheckBox.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showInfoDialog("Drill audio", "If this checkbox is checked, a sound will be played when you complete a set in a 'drill' run in the" +
                        " 'JUG' tab to indicate that it is complete.");
                return true;
            }
        });
        settingsRunDrillAudioCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (settingsRunDrillAudioCheckBox.isChecked()) {
                    myDb.updateData("SETTINGS", "MISC", "ID", "3", "on");
                } else {
                    myDb.updateData("SETTINGS", "MISC", "ID", "3", "off");
                }
            }
        });


        settingsKeepScreenOnDuringRuns = (CheckBox) findViewById(R.id.settingsKeepScreenOnDuringRun);
        settingsKeepScreenOnDuringRuns.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showInfoDialog("Screen timeout", "If this checkbox is checked, your screen will be prevented from timing out while you are in a run " +
                        "in the 'JUG' tab.");
                return true;
            }
        });
        settingsKeepScreenOnDuringRuns.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (settingsKeepScreenOnDuringRuns.isChecked()) {
                    myDb.updateData("SETTINGS", "MISC", "ID", "4", "on");

                } else {
                    myDb.updateData("SETTINGS", "MISC", "ID", "4", "off");

                }
            }
        });

        TextView bufferSecondsTV = (TextView) findViewById(R.id.bufferSecondsTV);
        bufferSecondsTV.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showInfoDialog("Time buffer", "This is the number of seconds between the time you click 'BEGIN' in the 'JUG' tab to the time your run" +
                        "timer begins, this gives you time to get ready to begin juggling.");
                return true;
            }
        });

        settingsBufferSecondsEditText = (EditText) findViewById(R.id.setBufferSecondsEditText);
        settingsBufferSecondsEditText.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showInfoDialog("Time buffer", "This is the number of seconds between the time you click 'BEGIN' in the 'JUG' tab to the time your run" +
                        "timer begins, this gives you time to get ready to begin juggling.");
                return true;
            }
        });
        settingsBufferSecondsEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {

                    //first we check to see if what was just input into the editText is composed only of digits
                    if (settingsBufferSecondsEditText.getText().toString().matches("[0-9]+")) {
                        //debugging
                        //Toast.makeText(MainActivity.this, "inHere", Toast.LENGTH_LONG).show();

                        //now we check to see if it is higher than 120, if it is, then we lower it to 200
                        //so, first we make it into an int
                        int numberInput = Integer.parseInt(settingsBufferSecondsEditText.getText().toString().replaceAll("[^0-9]", ""));
                        if (numberInput > 120) { //then we check if it is over 200
                            settingsBufferSecondsEditText.setText("120"); //if it is, we make it be 200
                        }
                        //this tells exactly where we want to update the max# of objects,
                        //      it goes table, column, id(0 is where we keep this particular add), dataToSend
                        myDb.updateData("SETTINGS", "MISC", "ID", "5", settingsBufferSecondsEditText.getText().toString());

                        //set the variable that is used when creating the buffer
                        totalBufferSeconds = Integer.parseInt(settingsBufferSecondsEditText.getText().toString().replaceAll("[^0-9]", ""));

                    } else {//if it was not composed only of digits, then we revert to whatever is in the database
                        settingsBufferSecondsEditText.setText(getCellFromDB("SETTINGS", "MISC", "ID", "5"));
                    }

                }
            }
        });


        //when this gets checked, we play the 'newrecord' audio whenever a 'personal best' is broken(unintentionalEnd or intentionalEnd) in endurance
        settingsRunEnduranceAudioBrakePB = (CheckBox) findViewById(R.id.settingsRunEndurAudioBrakePB);
        settingsRunEnduranceAudioBrakePB.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showInfoDialog("New personal best audio", "If this checkbox is checked, then while in an 'Endurance' run in the 'JUG' tab, two different sounds " +
                        "will be played when you beat either of your personal bests, for those ending in a intentionalEnd, or a unintentionalEnd. The personal" +
                        " best ending in a intentionalEnd is indicated with a guitar sound, the other sound indicates your personal best ending " +
                        "in a unintentionalEnd.");
                return true;
            }
        });
        settingsRunEnduranceAudioBrakePB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (settingsRunEnduranceAudioBrakePB.isChecked()) {
                    myDb.updateData("SETTINGS", "MISC", "ID", "8", "on");
                } else {
                    myDb.updateData("SETTINGS", "MISC", "ID", "8", "off");
                }
            }
        });

        //this puts a copy of the current .db being used by the app in the root directory of main storage so that
        //      it can be looked at and edited in 3rd party software
        settingsExportDatabaseButton = (Button) findViewById(R.id.settingsExportDatabaseBut);
        settingsExportDatabaseButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showInfoDialog("Export", "Clicking this button will export your entire database file(formic.db) to your main directory, this file can" +
                        " be viewed and edited with 3rd party database software. The exported file is just a copy of the file being used by" +
                        " the app and the app can continue to be used after exporting. In order to do this you need to give the app permission" +
                        " to access the phones storage, in the application manager in your phones settings. 'SQLite Magic' is a good android" +
                        " app for viewing/editing .db files.");
                return true;
            }
        });
        settingsExportDatabaseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    File sd = Environment.getExternalStorageDirectory();
                    File data = Environment.getDataDirectory();

                    if (sd.canWrite()) {
                        String currentDBPath = "/data/data/" + getPackageName() + "/databases/" + dbName;
                        String backupDBPath = dbName;
                        File currentDB = new File(currentDBPath);
                        File backupDB = new File(sd, backupDBPath);
                        //Toast.makeText(MainActivity.this, "DB exported!", Toast.LENGTH_LONG).show();

                        if (currentDB.exists()) {
                            FileChannel src = new FileInputStream(currentDB).getChannel();
                            FileChannel dst = new FileOutputStream(backupDB).getChannel();
                            dst.transferFrom(src, 0, src.size());
                            src.close();
                            dst.close();
                            Toast.makeText(MainActivity.this, "DB exported!", Toast.LENGTH_LONG).show();

                        }
                    } else {
                        showGivePermissionDialog();
                    }
                } catch (Exception e) {
                    Toast.makeText(MainActivity.this, "DB not exported", Toast.LENGTH_LONG).show();

                }
            }

        });


        //this takes a .db file at root directory of the main storage(oddly not the sd for some reason) and replaces the
        //      current .db being used by the app with it
        settingsImportDatabaseButton = (Button) findViewById(R.id.settingsImportDatabaseBut);
        settingsImportDatabaseButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showInfoDialog("Import", "Clicking this button will import a database file(formic.db) to your app from your devices main directory, " +
                        "the same place an exported database goes." +
                        " The database will only be exported if it is named 'formic.db'. " +
                        " If you import an edited database be sure it follows the same format as the original or the app may not run" +
                        " properly. In order to do this you need to give the app permission to access the phones storage in the" +
                        " application manager in your phones settings.");
                return true;
            }
        });
        settingsImportDatabaseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(MainActivity.this)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle("Sure?")
                        .setMessage("This will overwrite your current database with the one you are importing. Are you sure you want to " +
                                "do it?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                try {
                                    File sd = Environment.getExternalStorageDirectory();
                                    File data = Environment.getDataDirectory();

                                    if (sd.canWrite()) {

                                        String currentDBPath = dbName;
                                        String backupDBPath = "/data/data/" + getPackageName() + "/databases/" + dbName;
                                        File currentDB = new File(sd, currentDBPath);
                                        File backupDB = new File(backupDBPath);

                                        if (currentDB.exists()) {
                                            FileChannel src = new FileInputStream(currentDB).getChannel();
                                            FileChannel dst = new FileOutputStream(backupDB).getChannel();
                                            dst.transferFrom(src, 0, src.size());
                                            src.close();
                                            dst.close();
                                            fillAllFromDB();
                                            Toast.makeText(MainActivity.this, "DB imported!", Toast.LENGTH_LONG).show();
                                        } else {
                                            Toast.makeText(MainActivity.this, "There is no file to import in the devices main directory.", Toast.LENGTH_LONG).show();
                                        }
                                    } else {
                                        showGivePermissionDialog();
                                    }
                                } catch (Exception e) {
                                    Toast.makeText(MainActivity.this, "DB not imported", Toast.LENGTH_LONG).show();
                                }

                            }
                        })

                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .show();
            }

        });
        settingsClearPatternsBTN = (Button) findViewById(R.id.settingsClearPatternsBTN);
        settingsClearPatternsBTN.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showInfoDialog("Remove all patterns", "This button will remove all patterns and all of your personal history records. This is " +
                        "irreversible, in order to restore the original list you must reinstall the app.");
                return true;
            }
        });
        settingsClearPatternsBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAreYouSureDialog("PATTERNS");
            }
        });
        settingsClearModifiersBTN = (Button) findViewById(R.id.settingsClearModifiersBTN);
        settingsClearModifiersBTN.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showInfoDialog("Remove all modifiers", "This button will remove all modifiers and all of your personal history records. This is " +
                        "irreversible, in order to restore the original list you must reinstall the app.");
                return true;
            }
        });
        settingsClearModifiersBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAreYouSureDialog("MODIFIERS");
            }
        });
        settingsClearHistoryBTN = (Button) findViewById(R.id.settingsClearHistoryBTN);
        settingsClearHistoryBTN.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showInfoDialog("Remove all history", "This button will remove all of your personal history records. This is " +
                        "irreversible, in order to restore the original list you must reinstall the app.");
                return true;
            }
        });
        settingsClearHistoryBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAreYouSureDialog("HISTORY");
            }
        });

        settingsShowIntroBTN = (Button) findViewById(R.id.settingsShowIntroBTN);
        settingsShowIntroBTN.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showInfoDialog("Show intro", "This button will show the introduction window.");
                return true;
            }
        });
        settingsShowIntroBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showIntroDialog();
            }
        });
        settingsShowNotesBTN = (Button) findViewById(R.id.settingsShowNotesBTN);
        settingsShowNotesBTN.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showInfoDialog("Show notes", "This button will show the general notes window.");
                return true;
            }
        });
        settingsShowNotesBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showNotesDialog();
            }
        });

        settingsResetSettingsBTN = (Button) findViewById(R.id.settingsResetSettingsBTN);
        settingsResetSettingsBTN.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showInfoDialog("Restore default settings", "This button will return all settings to the default.");
                return true;
            }
        });
        settingsResetSettingsBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAreYouSureDialog("SETTINGS");
            }
        });
        settingsContactBTN = (Button) findViewById(R.id.settingsContactBTN);
        settingsContactBTN.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showInfoDialog("Contact", "This button will show contact information.");
                return true;
            }
        });
        settingsContactBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showContactDialog();
            }
        });

        TextView settingsSpecialThrowSequencesTV = (TextView) findViewById(R.id.settingsSpecialThrowSequencesTV);
        settingsSpecialThrowSequencesTV.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showInfoDialog("Special throw sequences", "Use the buttons below to add or remove special throw sequences.  For more information on" +
                        " special throw sequences, do a 2 second click on the 'MODIFIERS' header in the 'ADD' tab and scroll down to the 'SPECIAL" +
                        " THROW SEQUENCES' section.");
                return true;
            }
        });
        settingsSpecialThrowSequenceRemoveButton = (Button) findViewById(R.id.settingsSpecialThrowSequenceRemoveButton);
        settingsSpecialThrowSequenceRemoveButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showInfoDialog("Remove Special Sequence", "This button will open a window to remove a special sequence.");
                return true;
            }
        });
        settingsSpecialThrowSequenceRemoveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDeleteSpecialSequenceDialog();
            }
        });
        settingsSpecialThrowSequenceAddButton = (Button) findViewById(R.id.settingsSpecialThrowSequenceAddButton);
        settingsSpecialThrowSequenceAddButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showInfoDialog("Add Special Sequence", "This button will open a window to add a special sequence.");
                return true;
            }
        });
        settingsSpecialThrowSequenceAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddSpecialSequenceDialog();
            }
        });


        //now that we have everything we need for the structure of the settings tab,
        //      we can go ahead and populate stuff from our database

        settingsMaxNumberOfObjectsEditText.setText(getCellFromDB("SETTINGS", "MISC", "ID", "1"));                          //uses the number in the settings table of the database that indicates the
        //                                          max number of props to be used to populate the selection for prop number
        if ((getCellFromDB("SETTINGS", "MISC", "ID", "3").equals("on"))) {
            settingsRunDrillAudioCheckBox.setChecked(true);
        } else {
            settingsRunDrillAudioCheckBox.setChecked(false);
        }
        if ((getCellFromDB("SETTINGS", "MISC", "ID", "4").equals("on"))) {
            settingsKeepScreenOnDuringRuns.setChecked(true);
        } else {
            settingsKeepScreenOnDuringRuns.setChecked(false);

        }
        if ((getCellFromDB("SETTINGS", "MISC", "ID", "8").equals("on"))) {
            settingsRunEnduranceAudioBrakePB.setChecked(true);
        } else {
            settingsRunEnduranceAudioBrakePB.setChecked(false);

        }
        settingsBufferSecondsEditText.setText(getCellFromDB("SETTINGS", "MISC", "ID", "5"));

        if (!settingsBufferSecondsEditText.getText().toString().replaceAll("[^0-9]", "").equals("")
                ) {

        //set the variable that is used when creating the buffer
        totalBufferSeconds = Integer.parseInt(settingsBufferSecondsEditText.getText().toString().replaceAll("[^0-9]", ""));
    }
        //Log.d("lots", "4");
        fillObjectTypeATVsfromDB();

    }

    public void showDeleteSpecialSequenceDialog() {
        //showPatterns(); //just used temporarily for debugging, shows the patterns in a message which have input to the db
        //Toast.makeText(MainActivity.this, "Run", Toast.LENGTH_LONG).show();
        View view = (LayoutInflater.from(MainActivity.this)).inflate(R.layout.setsequencedeletedialog, null);

        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(MainActivity.this);
        alertBuilder.setView(view);

        setSpecialThrowSequenceList();

        TextView setSequenceDeleteDialogBoxTV = (TextView) view.findViewById(R.id.setSequenceDeleteDialogBoxTV);
        setSequenceDeleteDialogBoxTV.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showInfoDialog("Remove sequence", "Select the special throw sequence that you wish to remove. For more information on" +
                        " special throw sequences, click the 'MODIFIERS' header in the 'ADD' tab and scroll down to the 'SPECIAL" +
                        " THROW SEQUENCES' section.");
                return false;
            }
        });

        final Spinner setSequenceDeleteDialogBox = (Spinner) view.findViewById(R.id.setSequenceDeleteDialogBox);
        ArrayAdapter<String> setSequenceDeleteDialogBoxSpinnerAdapter = new ArrayAdapter<>(MainActivity.this,
                android.R.layout.select_dialog_item, setSequenceForSpinner);
        setSequenceDeleteDialogBoxSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        setSequenceDeleteDialogBox.setAdapter(setSequenceDeleteDialogBoxSpinnerAdapter);
        setSequenceDeleteDialogBox.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showInfoDialog("Remove sequence", "Select the special throw sequence that you wish to remove. For more information on" +
                        " special throw sequences, click the 'MODIFIERS' header in the 'ADD' tab and scroll down to the 'SPECIAL" +
                        " THROW SEQUENCES' section.");
                return false;
            }
        });
        setSequenceDeleteDialogBox.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Toast.makeText(getBaseContext(), parent.getItemAtPosition(position) + " selected", Toast.LENGTH_LONG).show();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        alertBuilder.setCancelable(true)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //this is what is currently in the sequenceSpinner, possibly typed by user or selected from autocomplete list by user
                        String currentlyInSequenceSpinner = setSequenceDeleteDialogBox.getSelectedItem().toString();

                        // addPatObjTypeForSpinner.clear();
                        //this is what is in our databse which must also match what is in the autocomplete unintentionalEnddown list for objectTypes
                        // addPatObjTypeForSpinner = Arrays.asList(getCellFromDB("SETTINGS","MISC","id","2").split(","));

                        // String[] bullshit = (getCellFromDB("SETTINGS","MISC","2").split(","));

                        //String[] n = new String[]{"google","microsoft","apple"};
                        List<String> listOfSequences = new ArrayList<>();
                        Collections.addAll(listOfSequences, (getCellFromDB("SETTINGS", "MISC", "ID", "10").split(",")));

                        //now we alphabetize the list
                        Collections.sort(listOfSequences, String.CASE_INSENSITIVE_ORDER);


                        //n = list.toArray(new String[list.size()]);

                        //List<String> shit = Arrays.asList(getCellFromDB("SETTINGS","MISC","ID","10").split(","));


                        //if what is is the database contains the current string we want to remove it from the database
                        if (containsCaseInsensitive(currentlyInSequenceSpinner, listOfSequences)) {


                            //first we remove the string from the array
                            listOfSequences.remove(currentlyInSequenceSpinner);

                            //then we make a string out of all the items of our array, each seperated by a comma
                            String arraySeperatedByCommas = android.text.TextUtils.join(",", listOfSequences);
//
//                    //then we update our database with our new string
                            myDb.updateData("SETTINGS", "MISC", "ID", "10", arraySeperatedByCommas);

                            //WE MIGHT WANT SOMETHING LIKE THIS, BUT PROBABLY NOT
                            //and we need to update the ATV to make it contain it
                            //fillObjectTypeATVsfromDB();

                            //a toast that tells us exactly what we have done
                            Toast.makeText(MainActivity.this, currentlyInSequenceSpinner + " removed from special throw sequences.", Toast.LENGTH_LONG).show();

                            //and then we set the objectType input to blank
                            //settingsObjectTypeATV.setText("");


                            //toast just for debuging
                            // Toast.makeText(MainActivity.this, "doesn't contain", Toast.LENGTH_LONG).show();
                        } else {

                            Toast.makeText(MainActivity.this, currentlyInSequenceSpinner + " is not a special throw sequence.", Toast.LENGTH_LONG).show();
                        }


                    }

                });
        Dialog dialog = alertBuilder.create();
        dialog.show();

    }

    public void showAddSpecialSequenceDialog() {
        View view = (LayoutInflater.from(MainActivity.this)).inflate(R.layout.setsequencenewdialog, null);

        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(MainActivity.this);
        alertBuilder.setView(view);

        TextView newSetSequenceInputDialogBoxTV = (TextView) view.findViewById(R.id.newSetSequenceInputDialogBoxTV);
        newSetSequenceInputDialogBoxTV.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showInfoDialog("New sequence", "Use the text field below to input a new special throw sequence. For more information on" +
                        " special throw sequences, click the 'MODIFIERS' header in the 'ADD' tab and scroll down to the 'SPECIAL" +
                        " THROW SEQUENCES' section.");
                return false;
            }
        });
        final EditText newSetSequenceInputDialogBox = (EditText) view.findViewById(R.id.newSetSequenceInputDialogBox);

        alertBuilder.setCancelable(true)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {


                    }
                });
        Dialog dialog = alertBuilder.create();
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(final DialogInterface dialog) {
                Button button = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                button.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {

                        //Toast.makeText(MainActivity.this,"clicked", Toast.LENGTH_LONG).show();
                        //this is what is currently in the objectTypeATV, possibly typed by user or selected from autocomplete list by user
                        String currentlyInSequenceEditText = newSetSequenceInputDialogBox.getText().toString();

                        if (isValidInput(currentlyInSequenceEditText)) {

                            setSpecialThrowSequenceList();
                            //we want to see if it is already in the autocomplete list or not, if it is not, then we will add it to the database
                            //String[] fullObjectTypeArray =  getCellFromDB("SETTINGS","MISC","ID","2").split(",");
                            //Toast.makeText(MainActivity.this, fullObjectTypeArray[0], Toast.LENGTH_LONG).show();
                            // Toast.makeText(MainActivity.this,"clicked", Toast.LENGTH_LONG).show();


                            if (containsCaseInsensitive(currentlyInSequenceEditText, setSequenceForSpinner)) {
                                //if the object type they input is already in the database
                                Toast.makeText(MainActivity.this, currentlyInSequenceEditText + " is already a special throw sequence.", Toast.LENGTH_LONG).show();

                            } else {


                                if (currentlyInSequenceEditText.contains(",")) {

                                    Toast.makeText(MainActivity.this, "Special throw sequences can't contain the symbol ','", Toast.LENGTH_LONG).show();

                                } else {
                                    if (currentlyInSequenceEditText.contains("-")) {

                                        Toast.makeText(MainActivity.this, "Special throw sequences can't contain the symbol '-'", Toast.LENGTH_LONG).show();

                                    } else {
                                        //but if it doesn't contain it, we need to make it contain it

                                        //first we make a string out of all the items of our array, each seperated by a comma
                                        //                                                      maybe this needs to be the string[] above
                                        String arraySeperatedByCommas = android.text.TextUtils.join(",", setSequenceForSpinner);

                                        //then we add our new objectType to the end of that string
                                        arraySeperatedByCommas = arraySeperatedByCommas + "," + currentlyInSequenceEditText;

                                        //then we update our database with our new string
                                        myDb.updateData("SETTINGS", "MISC", "ID", "10", arraySeperatedByCommas);

                                        //debugging
                                        //Toast.makeText(MainActivity.this, getCellFromDB("SETTINGS", "MISC", "ID", "2") + " isInDB", Toast.LENGTH_LONG).show();


                                        //I THINK WE DO NOT NEED THIS, IT IS LEFT OVER FROM WHERE WE COPIED THIS FROM
                                        //and we need to update the ATV to make it contain it
                                        //fillObjectTypeATVsfromDB();

                                        Toast.makeText(MainActivity.this, currentlyInSequenceEditText + " added to special throw sequences.", Toast.LENGTH_LONG).show();

                                        dialog.dismiss();
                                        //toast just for debuging
                                        // Toast.makeText(MainActivity.this, "doesn't contain", Toast.LENGTH_LONG).show();
                                    }
                                    //Toast.makeText(MainActivity.this, currentlyInObjectTypeATV, Toast.LENGTH_LONG).show();

                                }
                                //THIS IS JUST FOR DEBUGGING
                                //Toast.makeText(MainActivity.this, newAddModifierInputDialog.getText().toString(), Toast.LENGTH_LONG).show();

                            }
                        }
                    }
                });
            }
        });
        dialog.show();

    }

    public void showContactDialog() {
        View view = (LayoutInflater.from(MainActivity.this)).inflate(R.layout.showcontactdialog, null);

        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(MainActivity.this);
        alertBuilder.setView(view);


        TextView contactEmailTV = (TextView) view.findViewById(R.id.contactEmailTV);
        contactEmailTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                askIfWantToCopyToClipboard("tjthejuggler@gmail.com");
            }
        });
        contactEmailTV.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                askIfWantToCopyToClipboard("tjthejuggler@gmail.com");
                return false;
            }
        });
//        TextView contactBtcAddressTV = (TextView) view.findViewById(R.id.contactBtcAddressTV);
//        contactBtcAddressTV.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                askIfWantToCopyToClipboard("1PkoYuE54F5sM3eWgwPBH3zp25PmrvFyjo");
//            }
//        });
//        contactBtcAddressTV.setOnLongClickListener(new View.OnLongClickListener() {
//            @Override
//            public boolean onLongClick(View v) {
//                askIfWantToCopyToClipboard("1PkoYuE54F5sM3eWgwPBH3zp25PmrvFyjo");
//                return false;
//            }
//        });
/*        TextView contactBtcTV = (TextView) view.findViewById(R.id.contactBtcTV);
        contactBtcTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                askIfWantToCopyToClipboard("17xZyfxHvzZKH4sx5gVDFVfVhNQV5KyLeq");
            }
        });
        contactBtcTV.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                askIfWantToCopyToClipboard("17xZyfxHvzZKH4sx5gVDFVfVhNQV5KyLeq");
                return false;
            }
        });*/


        alertBuilder.setCancelable(true)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        Dialog dialog = alertBuilder.create();
        dialog.show();
    }

    public void askIfWantToCopyToClipboard(final String toCopy) {
        new AlertDialog.Builder(MainActivity.this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Copy?")
                .setMessage("Would you like to copy '" + toCopy + "' to clipboard?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                        clipboard.setPrimaryClip(ClipData.newPlainText("text", toCopy));

                        Toast.makeText(MainActivity.this, "'" + toCopy + "' copied to clipboard ", Toast.LENGTH_LONG).show();

                    }
                })

                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .show();
    }

    public void showGivePermissionDialog() {
        new AlertDialog.Builder(MainActivity.this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Permission")
                .setMessage("Permission denied. Would you like to grant permission?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent();
                        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", getPackageName(), null);
                        intent.setData(uri);
                        startActivity(intent);

                    }
                })

                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .show();
    }

    public void showAreYouSureDialog(final String tableName) {

        String messageString = "Are you sure you want to completely clear '" + tableName + "'? This can not be undone.";
        if (tableName.equals("SETTINGS")) {
            messageString = "Are you sure you want to reset settings to the default?";
        }
        if (tableName.equals("ENDSESSION")) {
            messageString = "Are you sure you want to end this session?";
        }

        new AlertDialog.Builder(MainActivity.this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Info")
                .setMessage(messageString)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (tableName.equals("ENDSESSION")) {
                            String mostRecentID = getColumnFromDB("SESSIONS", "ID").get(getColumnFromDB("SESSIONS", "ID").size() - 1);
                            myDb.updateData("SESSIONS", "ENDTIME", "ID", mostRecentID, dateAndTimeOfMostRecentRun());
                            histPopulateSessionDateAndTimesSpinner();
                            runFillSessionDateAndTimeTextView();
                            fillHistoryRecordsTextView();
                            showSessionSummary();
                        } else {
                            //if the tableName is PATTERNS or MODIFIERS, then it does this stuff as well as the stuff in the next modifieral
                            if (!tableName.equals("HISTORY") && !tableName.equals("SETTINGS")) {
                                myDb.clearTable(tableName);
                                //we need to close and establish a connection with the databse because we have just added columns
                                myDb.close();
                                myDb = new DatabaseHelper(MainActivity.this);
                                fillModifierMultiAutocompleteTextViewsFromDB();
                                modifierMATV.setText("");
                                setupMainInputs();
                            }
                            //if tableName is 'HISTORY' then it does this stuff
                            if (!tableName.equals("SETTINGS")) {
                                myDb.clearTable("HISTORYDRILL");
                                myDb.clearTable("HISTORYENDURANCE");
                                myDb.clearTable("SESSIONS");
                                //we need to close and establish a connection with the databse because we have just added columns
                                myDb.close();
                                myDb = new DatabaseHelper(MainActivity.this);
                                Toast.makeText(MainActivity.this, "'" + tableName + "' cleared.", Toast.LENGTH_LONG).show();
                                prepareHistoryTabActivity();
                                refreshStats();
                                runFillSessionDateAndTimeTextView();

                            } else {//so this is the only stuff it does if tableName is 'SETTINGS'
                                resetSettingsToDefault();
                                Toast.makeText(MainActivity.this, "Settings restored to default.", Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                })

                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .show();
    }

    public void showSessionSummary(){
        //this runs when the 'end session' button is clicked, it makes a dialog appear which contains the following information:
        //  -number of initial patterns
        //  -number of PBs broken
        //  -length of session(and amount of time spent juggling in session)
        //  -a header with the begin/end dates/times
        //  -and more stuff



        final AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
        // alertDialog.setIcon(android.R.drawable.ic_dialog_alert);
        alertDialog.setTitle("Session Summary.");
        //THIS SHOULD SHOW DRILL SUMMARY AND RESULTS
        alertDialog.setMessage(showSessionSummaryText());
        alertDialog.setButton(Dialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }

        });


        alertDialog.show();


    }
Boolean beingUsedForSummary = false;
    public String showSessionSummaryText(){

        beingUsedForSummary = true;
        //So, here is the plan, it is kinda hacky, but it will work,
        //  I am going to keep track of which checkboxes are currently checked in the history tab



        Boolean histMainInputsCheckboxRecord = histMainInputsCheckbox.isChecked();
        Boolean histObjectTypesOtherThanCurrentCheckboxRecord = histObjectTypesOtherThanCurrentCheckbox.isChecked();
        Boolean histNumberOfObjectsOtherThanCurrentCheckboxRecord = histNumberOfObjectsOtherThanCurrentCheckbox.isChecked();
        Boolean histPatternNamesOtherThanCurrentCheckboxRecord =  histPatternNamesOtherThanCurrentCheckbox.isChecked();
        Boolean histModifierCombinationOtherThanExactlyCurrentCheckboxRecord =  histModifierCombinationOtherThanExactlyCurrentCheckbox.isChecked();
        Boolean histModifierCombinationThatDoesntContainCurrentCheckboxRecord =  histModifierCombinationThatDoesntContainCurrentCheckbox.isChecked();
        Boolean histRunsNotInThisSessionCheckboxRecord =  histRunsNotInThisSessionCheckbox.isChecked();
        int histSessionDateAndTimesSpinnerRecord = histSessionDateAndTimesSpinner.getSelectedItemPosition();
        Boolean histEnduranceCheckboxRecord =  histEnduranceCheckbox.isChecked();
        Boolean histIntentionalEndCheckboxRecord =  histIntentionalEndCheckbox.isChecked();
        Boolean histUnintentionalEndCheckboxRecord =  histUnintentionalEndCheckbox.isChecked();
        Boolean histEndurancePersonalBestCheckboxRecord =  histEndurancePersonalBestCheckbox.isChecked();
        Boolean histEnduranceNotPersonalBestCheckboxRecord =  histEnduranceNotPersonalBestCheckbox.isChecked();
        Boolean histDrillCheckboxRecord =  histDrillCheckbox.isChecked();
        Boolean histRestartSetCheckboxRecord =  histRestartSetCheckbox.isChecked();
        Boolean histRestartSetDroplessCheckboxRecord =  histRestartSetDroplessCheckbox.isChecked();
        Boolean histRestartSetNotDroplessCheckboxRecord =  histRestartSetNotDroplessCheckbox.isChecked();
        Boolean histDontRestartCheckboxRecord =  histDontRestartCheckbox.isChecked();
        Boolean histInitialRunCheckboxRecord =  histInitialRunCheckbox.isChecked();
        Boolean histNonInitialRunCheckboxRecord =  histNonInitialRunCheckbox.isChecked();

        //now we set all the checkboxes to false except for the session checkbox and it's session spinner
        //  gets set to the most recent session
        histMainInputsCheckbox.setChecked(false);
        histObjectTypesOtherThanCurrentCheckbox.setChecked(false);
        histNumberOfObjectsOtherThanCurrentCheckbox.setChecked(false);
        histPatternNamesOtherThanCurrentCheckbox.setChecked(false);
        histModifierCombinationOtherThanExactlyCurrentCheckbox.setChecked(false);
        histModifierCombinationThatDoesntContainCurrentCheckbox.setChecked(false);
        histRunsNotInThisSessionCheckbox.setChecked(true);
        histSessionDateAndTimesSpinner.setSelection(histSessionDateAndTimesSpinner.getCount()-1);
        histEnduranceCheckbox.setChecked(false);
        histIntentionalEndCheckbox.setChecked(false);
        histUnintentionalEndCheckbox.setChecked(false);
        histEndurancePersonalBestCheckbox.setChecked(false);
        histEnduranceNotPersonalBestCheckbox.setChecked(false);
        histDrillCheckbox.setChecked(false);
        histRestartSetCheckbox.setChecked(false);
        histRestartSetDroplessCheckbox.setChecked(false);
        histRestartSetNotDroplessCheckbox.setChecked(false);
        histDontRestartCheckbox.setChecked(false);
        histInitialRunCheckbox.setChecked(false);
        histNonInitialRunCheckbox.setChecked(false);

        //now that we have set all the checkboxes where we want them, we update the history text view
        fillHistoryRecordsTextView();

        //  then i am going to analyze the text in historyRecordsTextView for all the data i want
        //this runs when the 'end session' button is clicked, it makes a dialog appear which contains the following information:

        String beginDateTime = "";
        String endDateTime = "";

        int differentPatternNamesCount = 0;
        List<String> differentPatternNamesList = new ArrayList<>(); //our list of patterns

        int differentObjNumbersCount = 0;
        List<String> differentObjNumbersList = new ArrayList<>(); //our list of object numbers

        int differentObjTypeCount = 0;
        List<String> differentObjTypeList = new ArrayList<>(); //our list of object numbers

        int sessionLength = 0;

        int drillRunTime = 0;
        int endurRunTime = 0;

        int drillRunCount = 0;
        int endurRunCount = 0;

        int endurRunIntentionalEndingCount = 0;
        int endurRunUnintentionalEndingCount = 0;

        int initialCount = 0;
        int personalBestCount = 0;




        //this splits up each different history item
        String[] historyItems = historyRecordsTextView.getText().toString().split("__________");

        //now we go through each item and gather all the info that we want from it
        for (int i = 0; i<historyItems.length-1; i++ ){
            Log.d("historyItems[i]", historyItems[i]);
            //ArrayList<String> linesOfHistoryItems =  AsList(historyItems[i].split("*"));
            List<String> linesOfHistoryItems = new ArrayList<String>(Arrays.asList(historyItems[i].split("`")));
            Log.d("linesOfHistoryItems", Integer.toString(linesOfHistoryItems.size()));
            Log.d("...ems.get(5)", linesOfHistoryItems.get(5));
            //Log.d("...ems.get(8)", linesOfHistoryItems.get(8));
//            ArrayList<String> linesOfHistoryItems = new ArrayList<String> ();
//            try {
//
//                BufferedReader br = new BufferedReader (new FileReader("infile"));
//
//                while ((historyItems[i] = br.readLine()) != null)
//                    linesOfHistoryItems.add(historyItems[i]);
//                br.close();
//                System.out.println (linesOfHistoryItems.size());
//            } catch (Exception e) {
//                System.out.println ("Exception: " + e);
//            }

            //these are the formats that the lines will be in based on whether the run was endurance or drill



            // based on the [5], we determine if this is a drill or endur and make a conditional to handle the drill and
            // endur differently
            if (linesOfHistoryItems.get(5).contains("Endurance")){
                //ENDURANCE

//                    Date(mm/dd/yy hh:mm:ss)[0]
//                    'Name: '[1]
                if (!differentPatternNamesList.contains(linesOfHistoryItems.get(1))){
                    differentPatternNamesList.add(linesOfHistoryItems.get(1));
                    differentPatternNamesCount++;
                }

//                    'Num of objs: '[2]

                if (!differentObjNumbersList.contains(linesOfHistoryItems.get(2))){
                    differentObjNumbersList.add(linesOfHistoryItems.get(2));
                    differentObjNumbersCount++;
                }
//                    'Obj type: '[3]
                if (!differentObjTypeList.contains(linesOfHistoryItems.get(3))){
                    differentObjTypeList.add(linesOfHistoryItems.get(3));
                    differentObjTypeCount++;
                }
//                    'Modifiers: '[4]
//                    'Endurance(initial)'[5]
                endurRunCount++;
                if (linesOfHistoryItems.get(5).contains("initial")){
                    initialCount++;
                }
//                    'Ended because '(reason)'/'Final thought was '(thought)'[6]
//                     Ended intentionally/unintentionally[7]
                if(linesOfHistoryItems.get(7).contains("unintentional")){
                    endurRunUnintentionalEndingCount++;
                }else{
                    endurRunIntentionalEndingCount++;
                }
//                    'Length: xx:xx:xx(PB)'[8]
                if(linesOfHistoryItems.get(8).contains("PB")){
                    personalBestCount++;
                }
                endurRunTime = endurRunTime + secondsFromFormattedTime
                        (linesOfHistoryItems.get(8).replace("Length: ","").replace("(PB)","").replace(" ","").replace("\n",""));

            }else{
                //DRILL
//                    Date(mm/dd/yy hh:mm:ss)[0]
//                    'Name: '[1]
                if (!differentPatternNamesList.contains(linesOfHistoryItems.get(1))){
                    differentPatternNamesList.add(linesOfHistoryItems.get(1));
                    differentPatternNamesCount++;
                }
//                    'Num of objs: '[2]
                if (!differentObjNumbersList.contains(linesOfHistoryItems.get(2))){
                    differentObjNumbersList.add(linesOfHistoryItems.get(2));
                    differentObjNumbersCount++;
                }
//                    'Obj type: '[3]
                if (!differentObjTypeList.contains(linesOfHistoryItems.get(3))){
                    differentObjTypeList.add(linesOfHistoryItems.get(3));
                    differentObjTypeCount++;
                }
//                    'Modifiers: '[4]
//                    'Drill(initial)'[5]
                drillRunCount++;
                if (linesOfHistoryItems.get(5).contains("initial")){
                    initialCount++;
                }
//                    Restart set on drops/Don't restart set on drops[6]
//                    'Sets: '[6]
//                    'Attempts: '[6]
//                    'Length: xx:xx:xx'[7]
                drillRunTime = drillRunTime + secondsFromFormattedTime
                        (linesOfHistoryItems.get(7).replace("Length: ","").replace(" ",""));
            }


        }





        //  then i will reset the checkboxes to where they were

        histMainInputsCheckbox.setChecked(histMainInputsCheckboxRecord);
        histObjectTypesOtherThanCurrentCheckbox.setChecked(histObjectTypesOtherThanCurrentCheckboxRecord);
        histNumberOfObjectsOtherThanCurrentCheckbox.setChecked(histNumberOfObjectsOtherThanCurrentCheckboxRecord);
        histPatternNamesOtherThanCurrentCheckbox.setChecked(histPatternNamesOtherThanCurrentCheckboxRecord);
        histModifierCombinationOtherThanExactlyCurrentCheckbox.setChecked(histModifierCombinationOtherThanExactlyCurrentCheckboxRecord);
        histModifierCombinationThatDoesntContainCurrentCheckbox.setChecked(histModifierCombinationThatDoesntContainCurrentCheckboxRecord);
        histRunsNotInThisSessionCheckbox.setChecked(histRunsNotInThisSessionCheckboxRecord);
        histSessionDateAndTimesSpinner.setSelection(histSessionDateAndTimesSpinnerRecord);
        histEnduranceCheckbox.setChecked(histEnduranceCheckboxRecord);
        histIntentionalEndCheckbox.setChecked(histIntentionalEndCheckboxRecord);
        histUnintentionalEndCheckbox.setChecked(histUnintentionalEndCheckboxRecord);
        histEndurancePersonalBestCheckbox.setChecked(histEndurancePersonalBestCheckboxRecord);
        histEnduranceNotPersonalBestCheckbox.setChecked(histEnduranceNotPersonalBestCheckboxRecord);
        histDrillCheckbox.setChecked(histDrillCheckboxRecord);
        histRestartSetCheckbox.setChecked(histRestartSetCheckboxRecord);
        histRestartSetDroplessCheckbox.setChecked(histRestartSetDroplessCheckboxRecord);
        histRestartSetNotDroplessCheckbox.setChecked(histRestartSetNotDroplessCheckboxRecord);
        histDontRestartCheckbox.setChecked(histDontRestartCheckboxRecord);
        histInitialRunCheckbox.setChecked(histInitialRunCheckboxRecord);
        histNonInitialRunCheckbox.setChecked(histNonInitialRunCheckboxRecord);

        beingUsedForSummary = false;
        fillHistoryRecordsTextView();
        String textToReturn = "different patterns: "+Integer.toString(differentPatternNamesCount)+"\n" +
                "different object numbers: "+Integer.toString(differentObjNumbersCount)+"\n" +
                "different object types: "+Integer.toString(differentObjTypeCount)+"\n" +
                "number of drill runs: "+Integer.toString(drillRunCount)+"\n" +
                "number of endurance runs: "+Integer.toString(endurRunCount)+"\n" +
                "time in runs: "+formattedTime(endurRunTime+drillRunTime)+"\n" +
                "time in drill runs: "+formattedTime(drillRunTime)+"\n" +
                "time in endurance runs: "+formattedTime(endurRunTime)+"\n" +
                "number of intentionally ended endurance runs: "+Integer.toString(endurRunIntentionalEndingCount)+"\n" +
                "number of unintentionally ended endurance runs: "+Integer.toString(endurRunUnintentionalEndingCount)+"\n" +
                "number of initial runs: "+Integer.toString(initialCount)+"\n" +
                "number of runs where a personal best was broken: "+Integer.toString(personalBestCount);


//        int sessionLength = 0;
//
//        int drillRunTime = 0;
//        int endurRunTime = 0;


        return textToReturn;
    }



    public void resetSettingsToDefault() {

        //this is for settings and such that need to not be empty before they get set by the user

        // use this format to access these: getCellFromDB("SETTINGS","MISC","ID",id);
        //since these use 'add' they are being added to the DB in order and that is what is determining their id#

        updateDataInDB("SETTINGS", "MISC", "ID", "1", "11"); //max number of props (id = 1)
        //maybe we dont want this line here
        updateDataInDB("SETTINGS", "MISC", "ID","2","Balls,Beanbags,Clubs,Rings"); //prop types (id = 2)

        updateDataInDB("SETTINGS", "MISC", "ID", "3", "on"); //audio that plays when a drill set is completed(on/off) (id = 3)
        updateDataInDB("SETTINGS", "MISC", "ID", "4", "on"); //keep screen on during runs(on/off) (id = 4)
        updateDataInDB("SETTINGS", "MISC", "ID", "5", "3"); //number of seconds before runs begin (id = 5)
        updateDataInDB("SETTINGS", "MISC", "ID", "6", "1"); //keeps track of the last selected number of objects in the main input for use next time
        //                                                          app is opened (id = 6)
        updateDataInDB("SETTINGS", "MISC", "ID", "7", "0"); //keeps track of the last selected prop type in the main input for use next time
        //                                                          app is opened (id = 7)
        updateDataInDB("SETTINGS", "MISC", "ID", "8", "on"); //keeps track of the last selected prop type in the main input for use next time
        //                                                          app is opened (id = 8)
        updateDataInDB("SETTINGS", "MISC", "ID", "9", "on"); //keeps track of whether or not the intro screen should be shown (id = 9)
        //keeps track of whether or not the intro screen should be shown (id = 10)
        updateDataInDB("SETTINGS", "MISC", "ID", "10", "ny,nyy,nyn,nny,ynn,yyn,yny");
        updateDataInDB("SETTINGS", "MISC", "ID", "11", "these are the notes.");
        prepareSettingsTabActivity();
    }

    public void fillAllFromDB() {
        fillModifierMultiAutocompleteTextViewsFromDB();
        //Log.d("z", "9");
        fillPatternMainInputsFromDB();

        //Log.d("lots", "5");
        fillObjectTypeATVsfromDB();

        //loadHistoryRecordsFromDB();

        updateAddPatternNumberOfObjectsFromDB();
    }

    public void setupTabHost() {

//  this just goes through and sets the labels for the tabs and connects them to different layouts

        final TabHost tab = (TabHost) findViewById(tabHost);
        tab.setup();

        TabHost.TabSpec spec1 = tab.newTabSpec("Jug");
        spec1.setIndicator("Jug");
        spec1.setContent(R.id.layout1);
        tab.addTab(spec1);

        TabHost.TabSpec spec2 = tab.newTabSpec("Hist");
        spec2.setIndicator("Hist");
        spec2.setContent(R.id.layout2);
        tab.addTab(spec2);

        TabHost.TabSpec spec3 = tab.newTabSpec("Add");
        spec3.setIndicator("Add");
        spec3.setContent(R.id.layout3);
        tab.addTab(spec3);

        TabHost.TabSpec spec4 = tab.newTabSpec("Set");
        spec4.setIndicator("Set");
        spec4.setContent(R.id.layout4);
        tab.addTab(spec4);


        TabHost.TabSpec spec5 = tab.newTabSpec("Sta");
        spec5.setIndicator("Sta");
        spec5.setContent(R.id.layout5);
        tab.addTab(spec5);

        //duplicate one of the above chunks, make a layout_ai and add the following in place in activity_main: (but change it to be
        //                  for ai/layout6
//        <include
//        android:id="@+id/layout5"
//        layout="@layout/layout_stats"
//        android:layout_width="match_parent"
//        android:layout_height="match_parent"></include>

        tab.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String tabId) {
                hideKeyboard();
                //just in case somehow the wakeLock is still active, we certainly want to turn it off if we are going to another tab
                if (wakeLock.isHeld()) {
                    wakeLock.release();
                }

                //there is nothing to type in the history tab activity, so we might as well hide the keyboard whenever we
                //      go over there to give us a larger viewing area
                if (tabId.equals("Stat")) {
                    refreshStats();
                    ScrollView statsScrollView = (ScrollView) findViewById(R.id.layout5);
                    statsScrollView.smoothScrollTo(0, 0);
                    //debugging
                    //Toast.makeText(MainActivity.this, "statTab", Toast.LENGTH_LONG).show();
                }

                //setTabColor(tab);

                updateModifierMATV();
                updateAddModifierFromModifierMATVselected();

            }
        });
        //  Toast.makeText(MainActivity.this, "settabcolor", Toast.LENGTH_LONG).show();
        setTabColor(tab);
    }

    public void removeAllNonNumberCharactersFromStatsEditTexts() {
        EditText[] statsEditTexts = new EditText[6];
        statsEditTexts[0] = statsMostDaysDoingAtLeastXBlankEveryYDaysXEditText;
        statsEditTexts[1] = statsMostDaysDoingAtLeastXBlankEveryYDaysYEditText;
        statsEditTexts[2] = statsNumberOfDaysDoingAtLeastXBlankEveryYDaysXEditText;
        statsEditTexts[3] = statsNumberOfDaysDoingAtLeastXBlankEveryYDaysYEditText;
        statsEditTexts[4] = statsAverageNumberOfBlankPerXEditText;
        statsEditTexts[5] = statsNumberOfBlankInLastXEditText;

        try {
            for (EditText e : statsEditTexts) {

                    e.setText(e.getText().toString().replaceAll("[^\\d]", ""));

            }
        }
        catch (Exception e) {
         /* This is a generic Exception handler which means it can handle
          * all the exceptions. This will execute if the exception is not
          * handled by previous catch blocks.
          */
            System.out.println("Exception occurred");
        }
    }

    public void refreshStats() {

        //Toast.makeText(getBaseContext(), "refreshStats() run" , Toast.LENGTH_LONG).show();


        removeAllNonNumberCharactersFromStatsEditTexts();

        statsAverageNumberOfBlankPerXEditText.requestFocus();
        statsAverageNumberOfBlankPerXEditText.clearFocus();

        //this format is used to keep our percentages from having to many decimal places
        DecimalFormat df = new DecimalFormat("####.###");


        //see these functions to find out what their items stand for. We are using them to fill in all the information in the stats
        //  tab, some values come directly from their functions, some values are other values added together or combined in some way.
        //
        int[] statsRunInfo = statsRunsInfo();

        int[] uniqueCombos = statsUniqueCombosOfModsAndEntriesWithHistoryInfo();

        statsTotalRunsNum.setText(Integer.toString(statsRunInfo[0] +
                statsRunInfo[100] +
                statsRunInfo[200] +
                statsRunInfo[300]));
        statsEndurRunsNum.setText(Integer.toString(statsRunInfo[0] +
                statsRunInfo[100]));
        statsEndurPersonalBestNum.setText(Integer.toString(statsRunInfo[2] + statsRunInfo[102]));
        statsEndurRunsIntentionalEndNum.setText(Integer.toString(statsRunInfo[0]));
        statsEndurRunsIntentionalEndPersonalBestNum.setText(Integer.toString(statsRunInfo[2]));
        statsEndurRunsUnintentionalEndNum.setText(Integer.toString(statsRunInfo[100]));
        statsEndurRunsUnintentionalEndPersonalBestNum.setText(Integer.toString(statsRunInfo[102]));
        statsInitialEndurRunsNum.setText(Integer.toString(statsRunInfo[3]));
        statsDrillRunsNum.setText(Integer.toString(statsRunInfo[200] +
                statsRunInfo[300]));
        statsDrillRunsRestartNum.setText(Integer.toString(statsRunInfo[300]));
        statsDrillRunsRestartDroplessNum.setText(Integer.toString(statsRunInfo[302]));
        statsDrillRunsDontRestartNum.setText(Integer.toString(statsRunInfo[200]));
        statsInitialDrillRunsNum.setText(Integer.toString(statsRunInfo[203]));


        statsTotalTimeRunsNum.setText(formattedTimeWithDays(statsRunInfo[1] +
                statsRunInfo[101] +
                statsRunInfo[201] +
                statsRunInfo[301]));
        statsTotalTimeEnduranceNum.setText(formattedTimeWithDays(statsRunInfo[1]
                + statsRunInfo[101]));
        statsTotalTimeEndurancePersonalBestNum.setText(formattedTimeWithDays(statsRunInfo[5] + statsRunInfo[105]));
        statsTotalTimeEnduranceIntentionalEndNum.setText(formattedTimeWithDays(statsRunInfo[1]));
        statsTotalTimeEnduranceIntentionalEndPersonalBestNum.setText(formattedTimeWithDays(statsRunInfo[5]));
        statsTotalTimeEnduranceUnintentionalEndNum.setText(formattedTimeWithDays(statsRunInfo[101]));
        statsTotalTimeEnduranceUnintentionalEndPersonalBestNum.setText(formattedTimeWithDays(statsRunInfo[105]));
        statsTotalTimeInitialEnduranceNum.setText(formattedTimeWithDays(statsRunInfo[8]));
        statsTotalTimeDrillsNum.setText(formattedTimeWithDays(statsRunInfo[201] +
                statsRunInfo[301]));
        statsTotalTimeDrillsRestartNum.setText(formattedTimeWithDays(statsRunInfo[301]));
        statsTotalTimeDrillsRestartDroplessNum.setText(formattedTimeWithDays(statsRunInfo[303]));
        statsTotalTimeDrillsDontRestartNum.setText(formattedTimeWithDays(statsRunInfo[201]));
        statsTotalTimeInitialDrillNum.setText(formattedTimeWithDays(statsRunInfo[202]));

        statsDaysSinceRunNum.setText(Integer.toString(min(min(statsRunInfo[4],
                statsRunInfo[104]),
                min(statsRunInfo[204],
                        statsRunInfo[304]))));
        statsDaysSinceEnduranceNum.setText(Integer.toString(min(statsRunInfo[4], statsRunInfo[104])));
        statsDaysSinceEnduranceBrokePersonalBestNum.setText(Integer.toString(min(statsRunInfo[7], statsRunInfo[107])));
        statsDaysSinceEnduranceIntentionalEndNum.setText(Integer.toString(statsRunInfo[4]));
        statsDaysSincePersonalBestIntentionalEndNum.setText(Integer.toString(statsRunInfo[7]));
        statsDaysSinceEnduranceUnintentionalEndNum.setText(Integer.toString(statsRunInfo[104]));
        statsDaysSincePersonalBestUnintentionalEndNum.setText(Integer.toString(statsRunInfo[107]));
        statsDaysSinceDrillNum.setText(Integer.toString(min(statsRunInfo[204], statsRunInfo[304])));
        statsDaysSinceDrillRestartNum.setText(Integer.toString(statsRunInfo[304]));
        statsDaysSinceDrillRestartDroplessNum.setText(Integer.toString(statsRunInfo[205]));
        statsDaysSinceDrillDontRestartNum.setText(Integer.toString(statsRunInfo[204]));
        statsDaysSinceInitialEnduranceNum.setText(Integer.toString(statsRunInfo[6]));
        statsDaysSinceInitialDrillNum.setText(Integer.toString(statsRunInfo[206]));


        statsAverageNumberOfBlankPerXEditTextNumberDaysRunNum.setText(df.format((double) statsRunInfo[408] / 100 +
                (double) statsRunInfo[413] / 100));
        statsAverageNumberOfBlankPerXEditTextNumberDaysEnduranceRunNum.setText(df.format((double) statsRunInfo[408] / 100));
        statsAverageNumberOfBlankPerXEditTextNumberDaysEnduranceRunPersonalBestNum.setText(df.format((double) statsRunInfo[410] / 100));
        statsAverageNumberOfBlankPerXEditTextNumberDaysEnduranceRunIntentionalEndNum.setText(df.format((double) statsRunInfo[409] / 100));
        statsAverageNumberOfBlankPerXEditTextNumberDaysEnduranceRunIntentionalEndPersonalBestNum.setText(df.format((double) statsRunInfo[411] / 100));
        statsAverageNumberOfBlankPerXEditTextNumberDaysEnduranceRunUnintentionalEndNum.setText(df.format((double) statsRunInfo[509] / 100));
        statsAverageNumberOfBlankPerXEditTextNumberDaysEnduranceRunUnintentionalEndPersonalBestNum.setText(df.format((double) statsRunInfo[511] / 100));
        statsAverageNumberOfBlankPerXEditTextNumberDaysInitialEnduranceNum.setText(df.format((double) statsRunInfo[412] / 100));
        statsAverageNumberOfBlankPerXEditTextNumberDaysDrillRunNum.setText(df.format((double) statsRunInfo[413] / 100));
        statsAverageNumberOfBlankPerXEditTextNumberDaysDrillRunRestartNum.setText(df.format((double) statsRunInfo[514] / 100));
        statsAverageNumberOfBlankPerXEditTextNumberDaysDrillRunRestartDroplessNum.setText(df.format((double) statsRunInfo[416] / 100));
        statsAverageNumberOfBlankPerXEditTextNumberDaysDrillRunDontRestartNum.setText(df.format((double) statsRunInfo[414] / 100));
        statsAverageNumberOfBlankPerXEditTextNumberDaysInitialDrillRunNum.setText(df.format((double) statsRunInfo[415] / 100));

        statsNumberOfBlankInLastXEditTextNumberDaysRunNum.setText(Integer.toString(statsRunInfo[418] + statsRunInfo[518] +
                statsRunInfo[423] + statsRunInfo[523]));
        statsNumberOfBlankInLastXEditTextNumberDaysEnduranceRunNum.setText(Integer.toString(statsRunInfo[418] + statsRunInfo[518]));
        statsNumberOfBlankInLastXEditTextNumberDaysEnduranceRunPersonalBestNum.setText(Integer.toString(statsRunInfo[420] + statsRunInfo[520]));
        statsNumberOfBlankInLastXEditTextNumberDaysEnduranceRunIntentionalEndNum.setText(Integer.toString(statsRunInfo[418]));
        statsNumberOfBlankInLastXEditTextNumberDaysEnduranceRunIntentionalEndPersonalBestNum.setText(Integer.toString(statsRunInfo[420]));
        statsNumberOfBlankInLastXEditTextNumberDaysEnduranceRunUnintentionalEndNum.setText(Integer.toString(statsRunInfo[518]));
        statsNumberOfBlankInLastXEditTextNumberDaysEnduranceRunUnintentionalEndPersonalBestNum.setText(Integer.toString(statsRunInfo[520]));
        statsNumberOfBlankInLastXEditTextNumberDaysInitialEnduranceNum.setText(Integer.toString(statsRunInfo[421]));
        statsNumberOfBlankInLastXEditTextNumberDaysDrillRunNum.setText(Integer.toString(statsRunInfo[423] + statsRunInfo[523]));
        statsNumberOfBlankInLastXEditTextNumberDaysDrillRunRestartNum.setText(Integer.toString(statsRunInfo[523]));
        statsNumberOfBlankInLastXEditTextNumberDaysDrillRunRestartDroplessNum.setText(Integer.toString(statsRunInfo[425]));
        statsNumberOfBlankInLastXEditTextNumberDaysDrillRunDontRestartNum.setText(Integer.toString(statsRunInfo[423]));
        statsNumberOfBlankInLastXEditTextNumberDaysInitialDrillRunNum.setText(Integer.toString(statsRunInfo[424]));


        statsPercentEndurEndIntentionalEndNum.setText(Integer.toString(statsRunInfo[49]));
        statsPercentEndurEndUnintentionalEndNum.setText(Integer.toString(statsRunInfo[149]));

        if (statsRunInfo[100] + statsRunInfo[0] - uniqueCombos[4] - uniqueCombos[5] == 0) {
            statsPercentEndurAnyPersonalBestBrokeNum.setText("n/a");
        } else {
            statsPercentEndurAnyPersonalBestBrokeNum.setText(Integer.toString(
                    (100 * (statsRunInfo[127] + statsRunInfo[27])) /
                            (statsRunInfo[100] + statsRunInfo[0] - uniqueCombos[4] - uniqueCombos[5])));
        }
        if (statsRunInfo[0] - uniqueCombos[4] == 0) {
            statsPercentEndurIntentionalEndPersonalBestBrokeNum.setText("n/a");
        } else {
            statsPercentEndurIntentionalEndPersonalBestBrokeNum.setText(Integer.toString((100 * statsRunInfo[27]) /
                    (statsRunInfo[0] - uniqueCombos[4])));
        }
        if (statsRunInfo[100] - uniqueCombos[5] == 0) {
            statsPercentEndurUnintentionalEndPersonalBestBrokeNum.setText("n/a");
        } else {
            statsPercentEndurUnintentionalEndPersonalBestBrokeNum.setText(Integer.toString((100 * statsRunInfo[127]) /
                    (statsRunInfo[100] - uniqueCombos[5])));
        }

        statsCurrentStreakPersonalBestNum.setText(Integer.toString(statsRunInfo[54]));
        statsCurrentStreakPersonalBestIntentionalEndNum.setText(Integer.toString(statsRunInfo[53]));
        statsCurrentStreakPersonalBestUnintentionalEndNum.setText(Integer.toString(statsRunInfo[153]));

        statsLongestStreakPersonalBestNum.setText(Integer.toString(statsRunInfo[56]));
        statsLongestStreakPersonalBestIntentionalEndNum.setText(Integer.toString(statsRunInfo[55]));
        statsLongestStreakPersonalBestUnintentionalEndNum.setText(Integer.toString(statsRunInfo[155]));

        statsNumberOfDaysDoingAtLeastXBlankEveryYDaysRunNum.setText(Integer.toString(statsRunInfo[34]));
        statsNumberOfDaysDoingAtLeastXBlankEveryYDaysEnduranceRunNum.setText(Integer.toString(statsRunInfo[35]));
        statsNumberOfDaysDoingAtLeastXBlankEveryYDaysEnduranceRunPersonalBestNum.setText(Integer.toString(statsRunInfo[36]));
        statsNumberOfDaysDoingAtLeastXBlankEveryYDaysEnduranceRunIntentionalEndNum.setText(Integer.toString(statsRunInfo[37]));
        statsNumberOfDaysDoingAtLeastXBlankEveryYDaysEnduranceRunIntentionalEndPersonalBestNum.setText(Integer.toString(statsRunInfo[38]));
        statsNumberOfDaysDoingAtLeastXBlankEveryYDaysEnduranceRunUnintentionalEndNum.setText(Integer.toString(statsRunInfo[137]));
        statsNumberOfDaysDoingAtLeastXBlankEveryYDaysEnduranceRunUnintentionalEndPersonalBestNum.setText(Integer.toString(statsRunInfo[138]));
        statsNumberOfDaysDoingAtLeastXBlankEveryYDaysInitialEnduranceNum.setText(Integer.toString(statsRunInfo[39]));

        statsNumberOfDaysDoingAtLeastXBlankEveryYDaysDrillRunNum.setText(Integer.toString(statsRunInfo[235]));
        statsNumberOfDaysDoingAtLeastXBlankEveryYDaysDrillRunRestartNum.setText(Integer.toString(statsRunInfo[337]));
        statsNumberOfDaysDoingAtLeastXBlankEveryYDaysDrillRunRestartDroplessNum.setText(Integer.toString(statsRunInfo[338]));
        statsNumberOfDaysDoingAtLeastXBlankEveryYDaysDrillRunDontRestartNum.setText(Integer.toString(statsRunInfo[237]));
        statsNumberOfDaysDoingAtLeastXBlankEveryYDaysInitialDrillRunNum.setText(Integer.toString(statsRunInfo[239]));

        statsMostDaysDoingAtLeastXBlankEveryYDaysRunNum.setText(Integer.toString(statsRunInfo[40]));
        statsMostDaysDoingAtLeastXBlankEveryYDaysEnduranceRunNum.setText(Integer.toString(statsRunInfo[41]));
        statsMostDaysDoingAtLeastXBlankEveryYDaysEnduranceRunPersonalBestNum.setText(Integer.toString(statsRunInfo[42]));
        statsMostDaysDoingAtLeastXBlankEveryYDaysEnduranceRunIntentionalEndNum.setText(Integer.toString(statsRunInfo[43]));
        statsMostDaysDoingAtLeastXBlankEveryYDaysEnduranceRunIntentionalEndPersonalBestNum.setText(Integer.toString(statsRunInfo[44]));
        statsMostDaysDoingAtLeastXBlankEveryYDaysEnduranceRunUnintentionalEndNum.setText(Integer.toString(statsRunInfo[143]));
        statsMostDaysDoingAtLeastXBlankEveryYDaysEnduranceRunUnintentionalEndPersonalBestNum.setText(Integer.toString(statsRunInfo[144]));
        statsMostDaysDoingAtLeastXBlankEveryYDaysInitialEnduranceNum.setText(Integer.toString(statsRunInfo[45]));

        statsMostDaysDoingAtLeastXBlankEveryYDaysDrillRunNum.setText(Integer.toString(statsRunInfo[241]));
        statsMostDaysDoingAtLeastXBlankEveryYDaysDrillRunRestartNum.setText(Integer.toString(statsRunInfo[343]));
        statsMostDaysDoingAtLeastXBlankEveryYDaysDrillRunRestartDroplessNum.setText(Integer.toString(statsRunInfo[344]));
        statsMostDaysDoingAtLeastXBlankEveryYDaysDrillRunDontRestartNum.setText(Integer.toString(statsRunInfo[243]));
        statsMostDaysDoingAtLeastXBlankEveryYDaysInitialDrillRunNum.setText(Integer.toString(statsRunInfo[245]));

        statsUniqueComboAnyNum.setText(Integer.toString(uniqueCombos[2]));
        statsUniqueComboEndurNum.setText(Integer.toString(uniqueCombos[0]));
        statsUniqueComboEndurIntentionalEndNum.setText(Integer.toString(uniqueCombos[4]));
        statsUniqueComboEndurUnintentionalEndNum.setText(Integer.toString(uniqueCombos[5]));
        statsUniqueComboDrillNum.setText(Integer.toString(uniqueCombos[1]));
        statsUniqueComboDrillRestartNum.setText(Integer.toString(uniqueCombos[6]));
        statsUniqueComboDrillDontRestartNum.setText(Integer.toString(uniqueCombos[7]));
        statsUniqueComboEndurAndDrillNum.setText(Integer.toString(uniqueCombos[3]));
    }




    int statsPreValueInt = 9999;


    public int[] statsRunsInfo() {

        int[] arrayToReturn = new int[600];
        //Endurance array index chart:
        //  [0] = number of endurance runs (intentionalEnd)
        //  [1] = number of seconds in endurance (intentionalEnd)
        //  [100] = number of endurance runs (unintentionalEnd)
        //  [101] = number of seconds in endurance (unintentionalEnd)
        //  [2] = personal best brakes than ended in a intentionalEnd counter
        //  [3] = number of initial endurance runs
        //  [102] = personal best brakes than ended in a unintentionalEnd counter
        //  [4] = days since a run ended with intentionalEnd
        //  [104] = days since a run ended with unintentionalEnd
        //  [5] = number of seconds in endurance (intentionalEnd) that a personal best was broken
        //  [105] = number of seconds in endurance (unintentionalEnd) that a personal best was broken
        //  [6] = days since an initial endurance run
        //  [7] = days since getting a personal best ending in a intentionalEnd
        //  [107] = days since getting a personal best ending in a unintentionalEnd
        //  [8] = number of seconds spent in initial endurance runs

        //  [34] = current streak of consecutive days doing a run
        //  [35] = current streak of consecutive days doing an endurance run
        //  [36] = current streak of consecutive days doing an endurance run where a personal best was broken
        //  [37] = current streak of consecutive days doing an endurance run that ended intentionally
        //  [137] = current streak of consecutive days doing an endurance run that ended unintentionally
        //  [38] = current streak of consecutive days doing an endurance run that ended intentionally where a personal best was broke
        //  [138] = current streak of consecutive days doing an endurance run that ended unintentionally where a personal best was broke
        //  [39] = current streak of consecutive days doing an initial run

        //  [40] = longest streak of consecutive days doing a run
        //  [41] = longest streak of consecutive days doing an endurance run
        //  [42] = longest streak of consecutive days doing an endurance run where a personal best was broken
        //  [43] = longest streak of consecutive days doing an endurance run that ended intentionally
        //  [143] = longest streak of consecutive days doing an endurance run that ended unintentionally
        //  [44] = longest streak of consecutive days doing an endurance run that ended intentionally where a personal best was broke
        //  [144] = longest streak of consecutive days doing an endurance run that ended unintentionally where a personal best was broke
        //  [45] = longest streak of consecutive days doing an initial run

        //  [49] = percent of runs that that we ended intentionally
        //  [149] = percent of runs that that we ended unintentionally
        //  [51] = percent of runs that broke personal bests
        //  [52] = percent of runs that broke personal bests and ended with intentionalEnd
        //  [152] = percent of runs that broke personal bests and ended with unintentionalEnd

        //  [53] = current personal best streak intentionalEnd
        //  [153] = current personal best streak unintentionalEnd
        //  [54] = current personal best streak all

        //  [55] = longest personal best streak intentionalEnd
        //  [155] = longest personal best streak unintentionalEnd
        //  [56] = longest personal best streak all

        // [408] = average number of endurance run per (user defined number) of days:
        // [409] = average number of endurance runs that ended intentionally per (user defined number) of days:
        // [509] = average number of endurance runs that ended unintentionally per (user defined number) of days:
        // [410] = average number of endurance runs where we broke a PB per (user defined number) of days:
        // [411] = average number of endurance runs where we ended intentionally and broke a PB per (user defined number) of days:
        // [511] = average number of endurance runs where we ended unintentionally and broke a PB per (user defined number) of days:
        // [412] = average number of initial endurance runs per (user defined number) of days:
        // [413] = average number of drill runs per (user defined number) of days:
        // [414] = average number of drill runs(don't restart) per (user defined number) of days:
        // [514] = average number of drill runs(restart) per (user defined number) of days:
        // [415] = average number of initial drill runs per (user defined number) of days:
        // [416] = average number of drill runs(restart that were dropless) per (user defined number) of days:

        // [418] = number of endurance runs that ended intentionally in the last (user defined number) of days:
        // [518] = number of endurance runs that ended unintentionally in the last (user defined number) of days:
        // [420] = number of endurance runs where we ended intentionally and broke a PB in the last (user defined number) of days:
        // [520] = number of endurance runs where we ended unintentionally and broke a PB in the last (user defined number) of days:
        // [421] = number of initial endurance runs in the last (user defined number) of days:
        // [423] = number of drill runs(don't restart) in the last (user defined number) of days:
        // [523] = number of drill runs(restart) in the last (user defined number) of days:
        // [424] = number of initial drill runs in the last (user defined number) of days:
        // [425] = number of drill runs(restart that were dropless) in the last (user defined number) of days:

        //Drill array index chart:---------------------------------------------
        //  [200] = number of drill runs (dontRestart)
        //  [201] = number of seconds in drill (dontRestart)
        //  [202] = number of seconds spent in initial drill sessions
        //  [203] = number of initial drill runs
        //  [300] = number of drill runs (restart)
        //  [301] = number of seconds in drill (restart)
        //  [302] = number of drill runs (restart, dropless)
        //  [303] = number of seconds in drill runs (restart, dropless)

        //  [204] = days since last drill run(dontRestart)
        //  [205] = days since last dropless drill run(restart)
        //  [304] = days since last drill run(restart)
        //  [206] = days since an initial drill run


        //  [235] = current streak of consecutive days doing a drill run
        //  [237] = current streak of consecutive days doing a drill run(don't restart set on unintentionalEnds)
        //  [337] = current streak of consecutive days doing a drill run(restart set on unintentionalEnds)
        //  [338] = current streak of consecutive days doing a drill run(restart set on unintentionalEnds) which was dropless
        //  [239] = current streak of consecutive days doing an initial drill run
        //
        //  [241] = longest streak of consecutive days doing a drill run
        //  [243] = longest streak of consecutive days doing a drill run(don't restart set on unintentionalEnds)
        //  [343] = longest streak of consecutive days doing a drill run(restart set on unintentionalEnds)
        //  [344] = longest streak of consecutive days doing a drill run(restart set on unintentionalEnds) which was dropless
        //  [245] = longest streak of consecutive days doing an initial drill run


        Arrays.fill(arrayToReturn, 0);
        arrayToReturn[4] = statsPreValueInt;
        arrayToReturn[104] = statsPreValueInt;
        arrayToReturn[6] = statsPreValueInt;
        arrayToReturn[7] = statsPreValueInt;
        arrayToReturn[107] = statsPreValueInt;

        //we go through every cell in the historyendurance table and count the number of times we find (endType)
        //Boolean personalBest = false;
        int currentWinnerIntentionalEnd = -1;
        int currentWinnerUnintentionalEnd = -1;
        int currentWinner = -1;

        int daysSinceEarliestEnduranceRun = 1;

        List<Integer> personalBestBreakFailureIntentionalEndSecondsFromNow = new ArrayList<>();
        List<Integer> personalBestBreakFailureUnintentionalEndSecondsFromNow = new ArrayList<>();

        List<Integer> personalBestBreakIntentionalEndSecondsFromNow = new ArrayList<>();
        List<Integer> personalBestBreakUnintentionalEndSecondsFromNow = new ArrayList<>();

        //POPULATE THESE LISTS
        List<String> datesOfEnduranceRunsEndingWithIntentionalEnd = new ArrayList<>();
        List<String> datesOfEnduranceRunsEndingWithUnintentionalEnd = new ArrayList<>();

        List<String> datesOfEnduranceRunsEndingWithIntentionalEndPersonalBest = new ArrayList<>();
        List<String> datesOfEnduranceRunsEndingWithUnintentionalEndPersonalBest = new ArrayList<>();

        List<String> datesOfInitialEnduranceRuns = new ArrayList<>();

//usable
        //this cycles through every column of of history endurance table, it contains the information on every endurance run
        //  that has happened. We skip the 1st column because it is the names of the different combinations of modifiers.
        for (int i = 2; i < myDb.getFromDB("HISTORYENDURANCE").getColumnCount(); i++) {

            //since we are going through the table 1 column at a time, we want to get the column name of the column that we are
            //  currently dealing with
            String columnName = myDb.getFromDB("HISTORYENDURANCE").getColumnName(i);

            //now we use that column name to fill up this list with each cell of the column. 1 cell = 1 item in the list. inside of a cell
            //  is all the information for each modifier/pattern combination. the format for endurance entries is:
            // (end explanation)(intentionalEnd/unintentionalEnd)(run length)(date/time), as many entries as needed are hed in a single cell, the format just
            //  repeats over and over.
            List<String> listOfAllCellsInTheColumn = getColumnFromDB("HISTORYENDURANCE", columnName);


            //now we cycle through each of the list items(the cells in our column), each one of these represents a possible different unique
            //  combination of modifiers & pattern. If it contains anything, then we know it is indeed a unique combination, once we know
            //  that, we want to find out if it contains a run that ended in a intentionalEnd and/or unintentionalEnd
            for (int j = 0; j < listOfAllCellsInTheColumn.size(); j++) {
                //Toast.makeText(getBaseContext(), listOfAllCellsInDB.get(j), Toast.LENGTH_LONG).show();


                //here we check to make sure that the list item(cell) is not null or empty, it has to have something in it or we don't
                //  want anything to do with it
                if (listOfAllCellsInTheColumn.get(j) != null && !listOfAllCellsInTheColumn.get(j).isEmpty()) {

                    //now we split the item(cell) on the parenthesis and put all the split parts into a list
                    String[] splitListOfAllCellsInDB = listOfAllCellsInTheColumn.get(j).split("\\)\\(");


                    //Here we are setting off to go gather all the endurance runs that ended intentionally and
                    //      we are counting them, and seeing how many days it has been since them, and how long we
                    //      spent in them

                    //we go through each item in the list
                    for (int k = 0; k < splitListOfAllCellsInDB.length; k++) {

                        String endType = splitListOfAllCellsInDB[k];

                        //it is set up so that all the stats that are for 'unintentionalEnd' are in 'arrayToReturn' indexes that are 100 higher than
                        //  their 'intentionalEnd' equivalents, so here we check to see which we are dealing with, and set our 'indexAdder'
                        if (endType.equals("intentionalEnd") || endType.equals("unintentionalEnd")) {

                            //if endType ends up being 'intentionalEnd', then we want this to stay at 0, but if it is unintentionalEnd, we want it
                            //      to be 100
                            int indexAdder = 0;


                            if (endType.equals("unintentionalEnd")) {
                                indexAdder = 100;
                                datesOfEnduranceRunsEndingWithUnintentionalEnd.add(splitListOfAllCellsInDB[k + 2]);
                            } else if (endType.equals("intentionalEnd")) {
                                datesOfEnduranceRunsEndingWithIntentionalEnd.add(splitListOfAllCellsInDB[k + 2]);

                            }
                            //Toast.makeText(getBaseContext(), "k is "+Integer.toString(k) , Toast.LENGTH_LONG).show();


                            //we add 1 to the counter of runs that ended in intentionalEnd
                            arrayToReturn[indexAdder + 0]++;

                            //we go through and get the number of seconds from each entry, we will later format this into DDD:hh:mm:ss

                            //this is where keep a running total of how much time was spent on endurance runs that ended with
                            //      a intentionalEnd. we take our current total number of seconds, arrayToReturn[1], and add it to itself plus
                            //      the number of seconds from the run that we just found the 'intentionalEnd' cell for. if the index
                            //      number of the 'intentionalEnd' is k, then we know(from our format above), that the time(in seconds) is 1
                            //      item index higher(k+1)
                            arrayToReturn[indexAdder + 1] = arrayToReturn[indexAdder + 1] + Integer.parseInt(splitListOfAllCellsInDB[k + 1]);

                            //this is getting how many days since doing an endurance run that ended in a intentionalEnd. If we find a run
                            //  that is more recent then our currently most recent intentionalEnd run, then we update the most recent intentionalEnd
                            //  run to whatever the more recent one was.
                            if (arrayToReturn[indexAdder + 4] > timeSince("days", splitListOfAllCellsInDB[k + 2])) {
                                arrayToReturn[indexAdder + 4] = timeSince("days", splitListOfAllCellsInDB[k + 2]);
                            }

                            //if k is less than 4 then we know we are in an initial run, we want to know how recent was the most recent
                            //      initial run that ended intentionally.
                            if (k < 4) {
                                if (arrayToReturn[6] > timeSince("days", splitListOfAllCellsInDB[k + 2])) {
                                    arrayToReturn[6] = timeSince("days", splitListOfAllCellsInDB[k + 2]);

                                    datesOfInitialEnduranceRuns.add(splitListOfAllCellsInDB[k + 2]);
                                    //this keeps track of the number of seconds spent in initial endurance runs
                                    arrayToReturn[8] = arrayToReturn[8] + Integer.parseInt(splitListOfAllCellsInDB[k + 1]);
                                }
                                //how many in the last (user defined number) of days
                                if (timeSince("days", splitListOfAllCellsInDB[k + 2]) < Integer.parseInt
                                        (statsNumberOfBlankInLastXEditText.getText().toString())) {
                                    arrayToReturn[indexAdder + 421]++;
                                }
                            }


                            //this keeps track of how many days ago was the longest ago run. this will be used in determining
                            //  the average number of personal best brakes in the past day/week/month.
                            if (daysSinceEarliestEnduranceRun < timeSince("days", splitListOfAllCellsInDB[k + 2])) {
                                daysSinceEarliestEnduranceRun = timeSince("days", splitListOfAllCellsInDB[k + 2]);
                            }

                            //Toast.makeText(getBaseContext(), "challenger is "+ splitListOfAllCellsInDB[k+1] +" and current winner is "+currentWinner , Toast.LENGTH_LONG).show();

                            //this is where we count how many endurance runs have ended in intentionalEnd/unintentionalEnd in the last day/week/month
                            if (timeSince("days", splitListOfAllCellsInDB[k + 2]) < 1) {
                                arrayToReturn[indexAdder + 18]++;//intentionalEnd last day counter
                            }
                            if (timeSince("days", splitListOfAllCellsInDB[k + 2]) < 8) {
                                arrayToReturn[indexAdder + 19]++;//intentionalEnd last week counter
                            }
                            if (timeSince("days", splitListOfAllCellsInDB[k + 2]) < 31) {
                                arrayToReturn[indexAdder + 20]++;//intentionalEnd last month counter
                            }
                            //how many in the last (user defined number) of days
                            if (timeSince("days", splitListOfAllCellsInDB[k + 2]) < Integer.parseInt
                                    (statsNumberOfBlankInLastXEditText.getText().toString())) {
                                arrayToReturn[indexAdder + 418]++;
                            }

                            if (endType.equals("intentionalEnd")) {
                                currentWinner = currentWinnerIntentionalEnd;
                            } else if (endType.equals("unintentionalEnd")) {
                                currentWinner = currentWinnerUnintentionalEnd;

                            }

                            //we take the item after the word 'intentionalEnd', this is the length of the run in seconds
                            //   if it is greater than the 'currentWinner'(easy to do at first since it starts at -1) and...
                            if (Integer.parseInt(splitListOfAllCellsInDB[k + 1]) > currentWinner) {
                                //..if this is a currentWinner that has been defined by us, not the default initial value of '-1',
                                if (currentWinner != -1) {
                                    //..then we go ahead and make arrayToReturn[7], which holds the number of days ago that the most
                                    //   recent run that ended in a intentionalEnd where a personal best was broken, equal to the date
                                    //   of the one that just broke the 'current winner'. btw, 'current winner' gets updated right
                                    //  after this conditional finishes.
                                    arrayToReturn[indexAdder + 7] = timeSince("days", splitListOfAllCellsInDB[k + 2]);

                                    //this is a counter that keeps tracks of how many personal bests we have broken and ended intentionally,
                                    //  it is going to be passed through and used as the 'alltime counter for personal best brakes that
                                    //      end in a intentionalEnd'
                                    arrayToReturn[indexAdder + 27]++;

                                    //this keeps track of all the dates/times that personal bests were broken
                                    if (endType.equals("intentionalEnd")) {
                                        //listOfDatesThatPersonalBestsWereBrokenIntentionalEnd.add(splitListOfAllCellsInDB[k + 2]);
                                        personalBestBreakIntentionalEndSecondsFromNow.add(timeSince("seconds", splitListOfAllCellsInDB[k + 2]));
                                        datesOfEnduranceRunsEndingWithIntentionalEndPersonalBest.add(splitListOfAllCellsInDB[k + 2]);
                                        //this is the 'personal best brakes that ended in a intentionalEnd' counter
                                        arrayToReturn[2]++;
                                        arrayToReturn[5] = arrayToReturn[5] + Integer.parseInt(splitListOfAllCellsInDB[k + 1]);


                                    } else if (endType.equals("unintentionalEnd")) {
                                        //listOfDatesThatPersonalBestsWereBrokenUnintentionalEnd.add(splitListOfAllCellsInDB[k + 2]);
                                        personalBestBreakUnintentionalEndSecondsFromNow.add(timeSince("seconds", splitListOfAllCellsInDB[k + 2]));
                                        datesOfEnduranceRunsEndingWithUnintentionalEndPersonalBest.add(splitListOfAllCellsInDB[k + 2]);
                                        //this is the 'personal best brakes that ended in a unintentionalEnd' counter
                                        arrayToReturn[102]++;
                                        arrayToReturn[105] = arrayToReturn[105] + Integer.parseInt(splitListOfAllCellsInDB[k + 1]);
                                    }

                                    //this is where we count how many endurance runs have ended in intentionalEnd and broke a personal best
                                    //      in the last day/week/month
                                    if (timeSince("days", splitListOfAllCellsInDB[k + 2]) < 1) {
                                        arrayToReturn[indexAdder + 24]++;//broke personal best, ended intentionally, last day counter
                                    }
                                    if (timeSince("days", splitListOfAllCellsInDB[k + 2]) < 8) {
                                        arrayToReturn[indexAdder + 25]++;//broke personal best, ended intentionally, last week counter
                                    }
                                    if (timeSince("days", splitListOfAllCellsInDB[k + 2]) < 31) {
                                        arrayToReturn[indexAdder + 26]++;//broke personal best, ended intentionally, last month counter
                                    }
                                    //how many in the last (user defined number) of days
                                    if (timeSince("days", splitListOfAllCellsInDB[k + 2]) < Integer.parseInt
                                            (statsNumberOfBlankInLastXEditText.getText().toString())) {
                                        arrayToReturn[indexAdder + 420]++;//broke personal best, ended intentionally/unintentionalEnd
                                    }


                                }
                                //Toast.makeText(getBaseContext(), "currentWinner is now "+ splitListOfAllCellsInDB[k+1] , Toast.LENGTH_LONG).show();

                                //we set the currentWinner to the number of seconds of whatever broke it.
                                if (endType.equals("intentionalEnd")) {
                                    currentWinnerIntentionalEnd = Integer.parseInt(splitListOfAllCellsInDB[k + 1]);
                                    ;
                                } else if (endType.equals("unintentionalEnd")) {
                                    currentWinnerUnintentionalEnd = Integer.parseInt(splitListOfAllCellsInDB[k + 1]);
                                    ;

                                }

                                //Toast.makeText(getBaseContext(), "currentWinner is now "+ splitListOfAllCellsInDB[k+1] , Toast.LENGTH_LONG).show();


                            } else {
                                if (endType.equals("intentionalEnd")) {

                                    //this is a list of the number of seconds from right now to the time of this failed opportunity
                                    //  to break a personal best in a run that ended intentionally
                                    personalBestBreakFailureIntentionalEndSecondsFromNow.add(timeSince("seconds", splitListOfAllCellsInDB[k + 2]));
                                } else if (endType.equals("unintentionalEnd")) {
                                    //this is a list of the number of seconds from right now to the time of this failed opportunity
                                    //  to break a personal best in a run that ended unintentionally
                                    personalBestBreakFailureUnintentionalEndSecondsFromNow.add(timeSince("seconds", splitListOfAllCellsInDB[k + 2]));
                                }


                            }


                        }
                    }


                }
                //I AM FAIRLY CONFIDENT THAT THE STUFF BELOW HERE SHOULD BE IN A FOR LOOP LIKE THE STUFF ABOVE SO THAT WE
                //  DON'T HAVE TO WRITE OUT THE STUFF FOR INTENTIONAL AND UNINTENTIONAL ENDINGS.
            }


        }//this bracket marks the end of going through all the cells in the HISTORYENDURANCE table, from here on, we are just using stuff
        //      we got from the table to come up with other values


        arrayToReturn[55] = statsGetLongestStreak("longest", personalBestBreakFailureIntentionalEndSecondsFromNow, personalBestBreakIntentionalEndSecondsFromNow);
        arrayToReturn[53] = statsGetLongestStreak("current", personalBestBreakFailureIntentionalEndSecondsFromNow, personalBestBreakIntentionalEndSecondsFromNow);
        arrayToReturn[155] = statsGetLongestStreak("longest", personalBestBreakFailureUnintentionalEndSecondsFromNow, personalBestBreakUnintentionalEndSecondsFromNow);
        arrayToReturn[153] = statsGetLongestStreak("current", personalBestBreakFailureUnintentionalEndSecondsFromNow, personalBestBreakUnintentionalEndSecondsFromNow);

        //now we combine the intentionalEnd and unintentionalEnd lists, and do it again
        List<Integer> personalBestBreakFailureBothSecondsFromNow = new ArrayList<>();
        personalBestBreakFailureBothSecondsFromNow.addAll(personalBestBreakFailureIntentionalEndSecondsFromNow);
        personalBestBreakFailureBothSecondsFromNow.addAll(personalBestBreakFailureUnintentionalEndSecondsFromNow);

        List<Integer> personalBestBreakBothSecondsFromNow = new ArrayList<>();
        personalBestBreakBothSecondsFromNow.addAll(personalBestBreakIntentionalEndSecondsFromNow);
        personalBestBreakBothSecondsFromNow.addAll(personalBestBreakUnintentionalEndSecondsFromNow);

        arrayToReturn[56] = statsGetLongestStreak("longest", personalBestBreakFailureBothSecondsFromNow, personalBestBreakBothSecondsFromNow);
        arrayToReturn[54] = statsGetLongestStreak("current", personalBestBreakFailureBothSecondsFromNow, personalBestBreakBothSecondsFromNow);


        //below here there is a recurring theme of checking to make sure that our denominators are not 0s, we
        //      have lots of denominators because we are getting percents here. If we find that a denominator is going
        //      to be a 0, then we just move on and leave the value shown as the default value indicating that there is no
        //      record to be shown. MAYBE AN 'N/A' WOULD BE GOOD HERE.

        //we are using this cycle loop to do the calculations for intentionalEnd and unintentionalEnd, some stuff is in here that is not using
        //  'indexAdder', it is just going to get calculated twice and the results will not change, it is just for the
        //  sake of staying organized.

        for (int cycle = 0; cycle < 2; cycle++) {

            int indexAdder = 0;
            if (cycle == 0) {
                indexAdder = 0;
            } else if (cycle == 1) {
                indexAdder = 100;
            }
            if (arrayToReturn[0] + arrayToReturn[100] != 0) {

                //this gets us the % of the time that we end in intentionalEnd/unintentionalEnd, it is the total number of runs that ended in a intentionalEnd/unintentionalEnd
                //  divided by the total number of runs. the 100* is so that we get a number for %, not a decimal.
                arrayToReturn[indexAdder + 49] = (100 * arrayToReturn[indexAdder + 0])
                        / (arrayToReturn[0] + arrayToReturn[100]); //FROM HERE WE WANT TO SUBTRACT THE NUMBER OF UNIQUE PATTERN/MODIFIER
                //                                                    COMBINATIONS SO THAT WE ARE NOT DOCKING THE % FOR INITIAL RUNS

            }

            //now we get the % our runs in which we break personal bests
            if (arrayToReturn[0] + arrayToReturn[100] != 0) {
                //we take the number of times we broke a PB and ended intentionally/unintentionalEnd plus the number of times we broke a PB and
                // ended intentionally/unintentionalEnd, and divide the total by the total number of runs
                arrayToReturn[51] = (100 * (arrayToReturn[27] + arrayToReturn[127]))
                        / (arrayToReturn[0] + arrayToReturn[100]);//FROM HERE WE WANT TO SUBTRACT THE NUMBER OF UNIQUE PATTERN/MODIFIER
                //                                                   COMBINATIONS SO THAT WE ARE NOT DOCKING THE % FOR INITIAL RUNS

            }

            //now we get the % of time that we ended intentionally/unintentionalEnd in which we broke a personal best
            //  -to clearify: in the runs in which we ended intentionally/unintentionalEnd, this is the percent of those that we broke
            //      our currently standing personal best time.

            if (arrayToReturn[indexAdder + 0] != 0) {
                //we are just dividing the number of times we ended intentionally/unintentionalEnd and broke a PB divided by total number of runs
                //  that we ended intentionally/unintentionalEnd
                arrayToReturn[indexAdder + 52] = (100 * arrayToReturn[indexAdder + 27]) / arrayToReturn[indexAdder + 0];//FROM HERE WE WANT TO
                // SUBTRACT THE NUMBER OF UNIQUE PATTERN/MODIFIER
                //                                                   COMBINATIONS SO THAT WE ARE NOT DOCKING THE % FOR INITIAL RUNS
            }


            //now we want to get the average number of personal bests per day/week/month for both/intentionalEnd/unintentionalEnd
            //  we want to get the earliest date(largest 'days since') out of all runs and use that as how far back
            //  to go when calculating the averages.

            //we don't want any of these to be 0s since we are going to be dividing by them to get our averages
            int totalDays = Math.max(1, daysSinceEarliestEnduranceRun);
            int totalWeeks = Math.max(1, daysSinceEarliestEnduranceRun / 7);
            int totalMonths = Math.max(1, daysSinceEarliestEnduranceRun / 30);

            //this would be the average number of endurance runs per day/week/month
            arrayToReturn[9] = (100 * (arrayToReturn[0] + arrayToReturn[100])) / totalDays;
            arrayToReturn[10] = (100 * (arrayToReturn[0] + arrayToReturn[100])) / totalWeeks;
            arrayToReturn[11] = (100 * (arrayToReturn[0] + arrayToReturn[100])) / totalMonths;
            //this is the average number of endur runs ending with a intentionalEnd/unintentionalEnd per day/week/month
            arrayToReturn[indexAdder + 12] = (100 * arrayToReturn[indexAdder + 0]) / totalDays;
            arrayToReturn[indexAdder + 13] = (100 * arrayToReturn[indexAdder + 0]) / totalWeeks;
            arrayToReturn[indexAdder + 14] = (100 * arrayToReturn[indexAdder + 0]) / totalMonths;


            //this would be the average number of endurance runs in which we broke a PB per day/week/month
            arrayToReturn[28] = (100 * (arrayToReturn[27] + arrayToReturn[127])) / totalDays;
            arrayToReturn[29] = (100 * (arrayToReturn[27] + arrayToReturn[127])) / totalWeeks;
            arrayToReturn[30] = (100 * (arrayToReturn[27] + arrayToReturn[127])) / totalMonths;
            //this would be the average number of endurance runs in which we ended intentionally/unintentionalEnd and broke a PB per day/week/month
            arrayToReturn[indexAdder + 31] = (100 * arrayToReturn[indexAdder + 27]) / totalDays;
            arrayToReturn[indexAdder + 32] = (100 * arrayToReturn[indexAdder + 27]) / totalWeeks;
            arrayToReturn[indexAdder + 33] = (100 * arrayToReturn[indexAdder + 27]) / totalMonths;

            int totalUserDefinedNumberOfDaysGroupsBlankAverage =
                    Math.max(1, daysSinceEarliestEnduranceRun / Integer.parseInt
                            (statsAverageNumberOfBlankPerXEditText.getText().toString()));


            //average number of .. per (user defined number) of days:
            //endurance run
            arrayToReturn[408] = (100 * (arrayToReturn[0] + arrayToReturn[100])) / totalUserDefinedNumberOfDaysGroupsBlankAverage;
            //intentionalEnd/unintentionalEnd
            arrayToReturn[indexAdder + 409] = (100 * arrayToReturn[indexAdder + 0]) / totalUserDefinedNumberOfDaysGroupsBlankAverage;
            // broke a PB
            arrayToReturn[410] = (100 * (arrayToReturn[27] + arrayToReturn[127])) / totalUserDefinedNumberOfDaysGroupsBlankAverage;
            //ended intentionally/unintentionalEnd and broke a PB per day/week/month
            arrayToReturn[indexAdder + 411] = (100 * arrayToReturn[indexAdder + 27]) / totalUserDefinedNumberOfDaysGroupsBlankAverage;
            //initial endurance
            arrayToReturn[412] = (100 * arrayToReturn[6]) / totalUserDefinedNumberOfDaysGroupsBlankAverage;
        }


        //this is a new list made by combining the intentionalEnd&unintentionalEnd lists
        List<String> datesOfEnduranceRuns = new ArrayList<>();
        datesOfEnduranceRuns.addAll(datesOfEnduranceRunsEndingWithIntentionalEnd);
        datesOfEnduranceRuns.addAll(datesOfEnduranceRunsEndingWithUnintentionalEnd);
        arrayToReturn[35] = statsStreakOfConsecutiveDays("current", datesOfEnduranceRuns);
        //this is a new list made by combining the intentionalEnd&unintentionalEnd personal best broken lists
        List<String> datesOfEnduranceRunsPersonalBest = new ArrayList<>();
        datesOfEnduranceRunsPersonalBest.addAll(datesOfEnduranceRunsEndingWithIntentionalEndPersonalBest);
        datesOfEnduranceRunsPersonalBest.addAll(datesOfEnduranceRunsEndingWithUnintentionalEndPersonalBest);
        arrayToReturn[36] = statsStreakOfConsecutiveDays("current", datesOfEnduranceRunsPersonalBest);
        //the intentionalEnd list we made above
        arrayToReturn[37] = statsStreakOfConsecutiveDays("current", datesOfEnduranceRunsEndingWithIntentionalEnd);
        //the unintentionalEnd list we made above
        arrayToReturn[137] = statsStreakOfConsecutiveDays("current", datesOfEnduranceRunsEndingWithUnintentionalEnd);
        //the intentionalEnd list of personal best brakes
        arrayToReturn[38] = statsStreakOfConsecutiveDays("current", datesOfEnduranceRunsEndingWithIntentionalEndPersonalBest);
        //the unintentionalEnd list of personal best brakes
        arrayToReturn[138] = statsStreakOfConsecutiveDays("current", datesOfEnduranceRunsEndingWithUnintentionalEndPersonalBest);
        //the initial list of endurance runs
        arrayToReturn[39] = statsStreakOfConsecutiveDays("current", datesOfInitialEnduranceRuns);
        arrayToReturn[3] = datesOfInitialEnduranceRuns.size();


        arrayToReturn[41] = statsStreakOfConsecutiveDays("longest", datesOfEnduranceRuns);
        arrayToReturn[42] = statsStreakOfConsecutiveDays("longest", datesOfEnduranceRunsPersonalBest);
        arrayToReturn[43] = statsStreakOfConsecutiveDays("longest", datesOfEnduranceRunsEndingWithIntentionalEnd);
        arrayToReturn[143] = statsStreakOfConsecutiveDays("longest", datesOfEnduranceRunsEndingWithUnintentionalEnd);
        arrayToReturn[44] = statsStreakOfConsecutiveDays("longest", datesOfEnduranceRunsEndingWithIntentionalEndPersonalBest);
        arrayToReturn[144] = statsStreakOfConsecutiveDays("longest", datesOfEnduranceRunsEndingWithUnintentionalEndPersonalBest);
        arrayToReturn[45] = statsStreakOfConsecutiveDays("longest", datesOfInitialEnduranceRuns);

        //***************NOW WE DO THE STUFF FOR THE DRILL RUNS**************

        int daysSinceEarliestDrillRun = 1;

        arrayToReturn[204] = statsPreValueInt;
        arrayToReturn[304] = statsPreValueInt;
        arrayToReturn[205] = statsPreValueInt;
        arrayToReturn[206] = statsPreValueInt;

        //POPULATE THESE LISTS
        List<String> datesOfDrillRunsRestart = new ArrayList<>();
        List<String> datesOfDrillRunsDontRestart = new ArrayList<>();

        List<String> datesOfDrillRunsRestartDropless = new ArrayList<>();

        List<String> datesOfInitialDrillRuns = new ArrayList<>();


        for (int i = 2; i < myDb.getFromDB("HISTORYDRILL").getColumnCount(); i++) {
            String columnName = myDb.getFromDB("HISTORYDRILL").getColumnName(i);
            List<String> listOfAllCellsInDB = getColumnFromDB("HISTORYDRILL", columnName);


            for (int j = 0; j < listOfAllCellsInDB.size(); j++) {
                //Toast.makeText(getBaseContext(), listOfAllCellsInDB.get(j), Toast.LENGTH_LONG).show();

                //here we check to make sure that a line has at least 1 number or 1 letter before even trying to
                //      add it as a siteswap, it prevents crashes
                if (listOfAllCellsInDB.get(j) != null && !listOfAllCellsInDB.get(j).isEmpty()) {

                    //now we split listOfAllCellsInDB.get(j) on the parenthesis and put all the split parts into a list

                    String[] splitListOfAllCellsInDB = listOfAllCellsInDB.get(j).split("\\)\\(");
                    //we go through each item in the list

                    for (int k = 0; k < splitListOfAllCellsInDB.length; k++) {


                        if (k % 4 == 0) {
                            splitListOfAllCellsInDB[k] = splitListOfAllCellsInDB[k].replace("(", "");

                            //it is set up so that all the stats that are for 'restart' are in 'arrayToReturn' indexes that are 100 higher than
                            //  their 'dontRestart' equivalents, so here we check to see which we are dealing with, and set our 'indexAdder'
                            int indexAdder = 0;
                            //if this is not a 0 then we know we restart set on unintentionalEnds, and so we want our indexAdder to be 100
                            if (!splitListOfAllCellsInDB[k].equals("0")) {
                                indexAdder = 100;
                                //here we keep track of the dates of all the 'don't restart set on unintentionalEnds runs'
                                datesOfDrillRunsRestart.add(splitListOfAllCellsInDB[k + 3]);
                                //since we know this is a 'restart set on unintentionalEnds' run, we go further and check to see if it is dropless,
                                //      we know that it is dropless if the number of sets is the same as the number of attempts
                                if (splitListOfAllCellsInDB[k].equals(splitListOfAllCellsInDB[k + 1])) {
                                    datesOfDrillRunsRestartDropless.add(splitListOfAllCellsInDB[k + 3]);
                                    //how many initial drill runs we have done in the last (user defined number) of days
                                    if (timeSince("days", splitListOfAllCellsInDB[k + 3]) < Integer.parseInt
                                            (statsNumberOfBlankInLastXEditText.getText().toString())) {
                                        arrayToReturn[425]++;
                                    }
                                    //this keeps track of the most recent dropless run(in restart)
                                    if (arrayToReturn[205] > timeSince("days", splitListOfAllCellsInDB[k + 3])) {
                                        arrayToReturn[205] = timeSince("days", splitListOfAllCellsInDB[k + 3]);
                                    }
                                    //number of drill runs (restart, dropless)
                                    arrayToReturn[302]++;

                                    //  [303] = number of seconds in drill runs (restart, dropless), it is the totalSeconds plus
                                    //      the number of sets*length of sets
                                    arrayToReturn[303] = arrayToReturn[303] + Integer.parseInt(splitListOfAllCellsInDB[k + 1]) *
                                            Integer.parseInt(splitListOfAllCellsInDB[k + 2]);

                                }
                            } else {//if it is anything but a 0, then we know we are doing a 'restart set on unintentionalEnds' run,
                                //so we keep track of the dates
                                datesOfDrillRunsDontRestart.add(splitListOfAllCellsInDB[k + 3]);

                            }


                            //...so we add one to the counter
                            arrayToReturn[indexAdder + 200]++;

                            //and we take the current totalSeconds plus the number of sets*length of sets

                            arrayToReturn[indexAdder + 201] = arrayToReturn[indexAdder + 201] + (Integer.parseInt(splitListOfAllCellsInDB[k + 1]) *
                                    Integer.parseInt(splitListOfAllCellsInDB[k + 2]));
                            // plus any extra seconds that happened due to failed attempts, but we only add those if there is a ',', and there is only a ',' if this is
                            //  a 'restart set on unintentionalEnds' session
                            if (splitListOfAllCellsInDB[k].contains(",")) {
                                arrayToReturn[indexAdder + 201] = arrayToReturn[indexAdder + 201] + Integer.parseInt(splitListOfAllCellsInDB[k].split(",")[1]);

                            }

                            if (arrayToReturn[indexAdder + 204] > timeSince("days", splitListOfAllCellsInDB[k + 3])) {
                                arrayToReturn[indexAdder + 204] = timeSince("days", splitListOfAllCellsInDB[k + 3]);
                            }
                            //if k is less than 4 then we know we are in an initial run,
                            if (k < 4) {
                                if (arrayToReturn[206] > timeSince("days", splitListOfAllCellsInDB[k + 3])) {
                                    arrayToReturn[206] = timeSince("days", splitListOfAllCellsInDB[k + 3]);
                                }
                                datesOfInitialDrillRuns.add(splitListOfAllCellsInDB[k + 3]);
                                //how many in the last (user defined number) of days
                                if (timeSince("days", splitListOfAllCellsInDB[k + 3]) < Integer.parseInt
                                        (statsNumberOfBlankInLastXEditText.getText().toString())) {
                                    arrayToReturn[424]++;//broke personal best, ended intentionally/unintentionalEnd
                                }
                                //this keeps track of the number of seconds spent in initial drill runs
                                arrayToReturn[202] = arrayToReturn[202] + Integer.parseInt(splitListOfAllCellsInDB[k + 1]);
                            }

                            //this keeps track of how many days ago was the longest ago run. this will be used in determining
                            //  the average number of personal best brakes in the past day/week/month.
                            if (daysSinceEarliestDrillRun < timeSince("days", splitListOfAllCellsInDB[k + 3])) {
                                daysSinceEarliestDrillRun = timeSince("days", splitListOfAllCellsInDB[k + 3]);
                            }

                            //this is where we count how many drill that have been "don't restart" on unintentionalEnd which have
                            //       ended in the last day/week/month
                            if (timeSince("days", splitListOfAllCellsInDB[k + 3]) < 1) {
                                arrayToReturn[indexAdder + 218]++;
                            }
                            if (timeSince("days", splitListOfAllCellsInDB[k + 3]) < 8) {
                                arrayToReturn[indexAdder + 219]++;
                            }
                            if (timeSince("days", splitListOfAllCellsInDB[k + 3]) < 31) {
                                arrayToReturn[indexAdder + 220]++;
                            }

                            //how many in the last (user defined number) of days
                            if (timeSince("days", splitListOfAllCellsInDB[k + 3]) < Integer.parseInt
                                    (statsNumberOfBlankInLastXEditText.getText().toString())) {
                                arrayToReturn[indexAdder + 423]++;//broke personal best, ended intentionally/unintentionalEnd
                            }


                            //   for initial run we do not care what kind of drill it is, so it is outside of the conditional
                            //       check above.


                        }


                    }


                }
            }
        }

        int totalDays = Math.max(1, daysSinceEarliestDrillRun);
        int totalWeeks = Math.max(1, daysSinceEarliestDrillRun / 7);
        int totalMonths = Math.max(1, daysSinceEarliestDrillRun / 30);

        //this would be the average number of drill runs per day/week/month
        arrayToReturn[209] = (100 * (arrayToReturn[200] + arrayToReturn[300])) / totalDays;
        arrayToReturn[210] = (100 * (arrayToReturn[200] + arrayToReturn[300])) / totalWeeks;
        arrayToReturn[211] = (100 * (arrayToReturn[200] + arrayToReturn[300])) / totalMonths;
        //restart
        arrayToReturn[312] = (100 * arrayToReturn[300]) / totalDays;
        arrayToReturn[313] = (100 * arrayToReturn[300]) / totalWeeks;
        arrayToReturn[314] = (100 * arrayToReturn[300]) / totalMonths;
        //don't restart
        arrayToReturn[212] = (100 * arrayToReturn[200]) / totalDays;
        arrayToReturn[213] = (100 * arrayToReturn[200]) / totalWeeks;
        arrayToReturn[214] = (100 * arrayToReturn[200]) / totalMonths;

        int totalUserDefinedNumberOfDaysGroupsBlankAverage =
                Math.max(1, daysSinceEarliestDrillRun / Integer.parseInt
                        (statsAverageNumberOfBlankPerXEditText.getText().toString()));
        //average number of .. per (user defined number) of days:
        //drill runs
        arrayToReturn[413] = (100 * (arrayToReturn[200] + arrayToReturn[300])) / totalUserDefinedNumberOfDaysGroupsBlankAverage;
        //don't restart runs
        arrayToReturn[414] = (100 * (arrayToReturn[200])) / totalUserDefinedNumberOfDaysGroupsBlankAverage;
        //restart runs
        arrayToReturn[514] = (100 * (arrayToReturn[300])) / totalUserDefinedNumberOfDaysGroupsBlankAverage;
        //initial
        arrayToReturn[415] = (100 * (arrayToReturn[206])) / totalUserDefinedNumberOfDaysGroupsBlankAverage;
        //dropless restart runs
        arrayToReturn[416] = (100 * (datesOfDrillRunsRestartDropless.size())) / totalUserDefinedNumberOfDaysGroupsBlankAverage;


        List<String> datesOfDrillRuns = new ArrayList<>();
        datesOfDrillRuns.addAll(datesOfDrillRunsRestart);
        datesOfDrillRuns.addAll(datesOfDrillRunsDontRestart);
        arrayToReturn[235] = statsStreakOfConsecutiveDays("current", datesOfDrillRuns);
        arrayToReturn[237] = statsStreakOfConsecutiveDays("current", datesOfDrillRunsDontRestart);
        arrayToReturn[337] = statsStreakOfConsecutiveDays("current", datesOfDrillRunsRestart);
        arrayToReturn[338] = statsStreakOfConsecutiveDays("current", datesOfDrillRunsRestartDropless);
        arrayToReturn[239] = statsStreakOfConsecutiveDays("current", datesOfInitialDrillRuns);
        arrayToReturn[203] = datesOfInitialDrillRuns.size();


        arrayToReturn[241] = statsStreakOfConsecutiveDays("longest", datesOfDrillRuns);
        arrayToReturn[243] = statsStreakOfConsecutiveDays("longest", datesOfDrillRunsDontRestart);
        arrayToReturn[343] = statsStreakOfConsecutiveDays("longest", datesOfDrillRunsRestart);
        arrayToReturn[344] = statsStreakOfConsecutiveDays("longest", datesOfDrillRunsRestartDropless);
        arrayToReturn[245] = statsStreakOfConsecutiveDays("longest", datesOfInitialDrillRuns);

        List<String> datesOfRuns = new ArrayList<>();
        datesOfRuns.addAll(datesOfDrillRunsRestart);
        datesOfRuns.addAll(datesOfDrillRunsDontRestart);
        datesOfRuns.addAll(datesOfEnduranceRunsEndingWithIntentionalEnd);
        datesOfRuns.addAll(datesOfEnduranceRunsEndingWithUnintentionalEnd);
        //this is a combined list of endurance dates and drill dates, so it will need to me moved below the drill dates lits
        arrayToReturn[34] = statsStreakOfConsecutiveDays("current", datesOfRuns);
        //everything below here is the same as the above, just with 'longest' instead of 'current'
        arrayToReturn[40] = statsStreakOfConsecutiveDays("longest", datesOfRuns);


        return arrayToReturn;

    }

    public int statsStreakOfConsecutiveDays(String streakType, List<String> datesList) {

        int toReturn = 0;

        SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yy HH:mm:ss");
        ////Log.d("myTag", "1");
        List<Date> dateListFormatted = new ArrayList<>();
        for (int i = 0; i < datesList.size(); i++) {
            try {
                dateListFormatted.add(formatter.parse(datesList.get(i)));

            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        ////Log.d("myTag", "2");
        Date[] dateToUseCurrent = new Date[2];
        Date dateToUseLongest = null;
        if (streakType.equals("current")) {
            try {
                //if we are doing current, then we only want to use todays date
                dateToUseCurrent[0] = formatter.parse(currentDateAndTime());
                Calendar c = Calendar.getInstance();
                c.setTime(dateToUseCurrent[0]);
                ////Log.d("myTag", "dateToUseCurrent[0] = "+dateToUseCurrent[0].toString());
                c.add(Calendar.DATE, -1);
                dateToUseCurrent[1] = c.getTime();
                ////Log.d("myTag", "dateToUseCurrent[0] = "+dateToUseCurrent[0].toString());
            } catch (ParseException e) {
                e.printStackTrace();
            }
            ////Log.d("myTag", "3");
            int minimumOccurances = Integer.parseInt(statsNumberOfDaysDoingAtLeastXBlankEveryYDaysXEditText.getText().toString());
            int numberOfDays = Integer.parseInt(statsNumberOfDaysDoingAtLeastXBlankEveryYDaysYEditText.getText().toString());
            toReturn = Math.max(statsCalculateStreakFromGivenDate(minimumOccurances, numberOfDays, dateToUseCurrent[0], dateListFormatted),
                    statsCalculateStreakFromGivenDate(minimumOccurances, numberOfDays, dateToUseCurrent[1], dateListFormatted));


        } else if (streakType.equals("longest")) {
            ////Log.d("myTag", "4");
            int minimumOccurances = Integer.parseInt(statsMostDaysDoingAtLeastXBlankEveryYDaysXEditText.getText().toString());
            //if we are doing longest, then we want to cycle through every date in our list
            int numberOfDays = Integer.parseInt(statsMostDaysDoingAtLeastXBlankEveryYDaysYEditText.getText().toString());

            for (int i = 0; i < datesList.size(); i++) {
                dateToUseLongest = dateListFormatted.get(i);
                if (statsCalculateStreakFromGivenDate(minimumOccurances, numberOfDays, dateToUseLongest, dateListFormatted) >= toReturn) {

                    toReturn = statsCalculateStreakFromGivenDate(minimumOccurances, numberOfDays, dateToUseLongest, dateListFormatted);

                }
            }
            //the reason we add 1 here, but not above in the 'current' section, is that this section has a day to start with,
            //  current is just using today to start with, and maybe it doesn't even have 1 consecutive day

            //toReturn++;

        }

        return toReturn;
    }

    public int statsCalculateStreakFromGivenDate(int minimumOccurances, int numberOfDays, Date dateToUse, List<Date> dateListFormatted) {

//        SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yy HH:mm:ss");
//        ////Log.d("myTag", "1");
//        List<Date> dateListFormatted = new ArrayList<>();
//        for(int i = 0; i<datesList.size();i++){
//            try {
//                dateListFormatted.add(formatter.parse(datesList.get(i)));
        ////Log.d("myTag", "5");

        //this is the day after the latest date in our list of consecutive days, in order to expand our list we must find a date
        //  that matches this(or calLow)

        //these arrays are the dates on either side of the dateToUse, the number of dates are based on what the user has in the editText
        //  that was passed through as 'numberOfDays'
        Calendar[] calHigh = new Calendar[numberOfDays];
        for (int i = 0; i < calHigh.length; i++) {
            calHigh[i] = Calendar.getInstance();
            calHigh[i].setTime(dateToUse);
            ////Log.d("myTag", "dateToUse = "+calHigh[i].toString());
            calHigh[i].add(Calendar.DATE, i + 1);
            //if we have problems, this might be why
            //calHigh[i].add(Calendar.DATE, i+1);
        }

        Calendar[] calLow = new Calendar[numberOfDays];
        for (int i = 0; i < calLow.length; i++) {
            calLow[i] = Calendar.getInstance();
            calLow[i].setTime(dateToUse);
            calLow[i].add(Calendar.DATE, -i);
            //calLow[i].add(Calendar.DATE, -(i+1));
        }

//
//        Calendar calOriginal = Calendar.getInstance();
//        calOriginal.setTime(dateToUse);


        ////Log.d("myTag", "calHigh pre initial add = "+calHigh.toString());

        ////Log.d("myTag", "calHigh post initial add = "+calHigh.toString());
        //this is the day after the latest date in our list of consecutive days, in order to expand our list we must find a date
        //  that matches this(or calHigh)

        ////Log.d("myTag", "calLow post initial add = "+calLow.toString());
        //this is the number of consecutive days, this is what we are going to return
        int numConsecutive = 0;

        //if we ever find that there are no more dates on our current consecutive list, then this will be made false, and we will be free
        //      from our while loop, and we will go ahead and return the length of consecutivity
        Boolean continueChecking = true;
        int calHighOccuranceCount = 0;
        int calLowOccuranceCount = 0;
        ////Log.d("myTag", "6");
        //so long as this is true, we are continuing to look for consecutive dates
        while (continueChecking) {
            //we go through the entire list of dates, looking for one that is the day after our latest consecutive date, or below(with calLow) we
            //  are looking for a date that is the day before our list of consecutive dates
            for (int i = 0; i < dateListFormatted.size(); i++) {
                Boolean alreadyHaveMatch = false;
                ////Log.d("myTag", "dateListFormatted.get(i) = "+dateListFormatted.get(i));
                ////Log.d("myTag", "7");
                Calendar calThis = Calendar.getInstance();
                calThis.setTime(dateListFormatted.get(i));
                ////Log.d("myTag", "calThis.getTime() = "+calThis.getTime());
                ////Log.d("myTag", "calHigh.getTime() = "+calHigh.getTime());
                //WHAT WE NEED TO DO TO MAKE THE NUMBER OF DAY PERIODS SET AS THE USER DEFINED EDITTEXT:
                //  -we need to know which editText we are using
                //  -we need to make calHigh, and calLow into arrays. The length of these arrays are to be equal to the
                //      number in the editText
                //  -we use a for loop to check all the dates of our arrays
                //  -it does not matter which of the date is a match, just that at least 1 of the dates was a match,
                //      once that happens, we change all the dates in that calendars array, and restart at i = 0
                //              -when we change all the dates, we move to the period of dates more extreme in the direction of that cal
                for (int j = 0; j < numberOfDays; j++) {
                    if (calThis.get(Calendar.YEAR) == calHigh[j].get(Calendar.YEAR) &&
                            calThis.get(Calendar.DAY_OF_YEAR) == calHigh[j].get(Calendar.DAY_OF_YEAR)) {

                        calHighOccuranceCount++;

                        if (calHighOccuranceCount == minimumOccurances) {
                            calHighOccuranceCount = 0;
                            calLowOccuranceCount = 0;
                            //we keep track of whether or not we have a match because if we do have one then we want to update the
                            //      cal[] that got matched and start our original list over at the begining
                            alreadyHaveMatch = true;
                            //every time we find a date that we are looking for, we add 1 to our counter
                            numConsecutive = numConsecutive + numberOfDays;
                            ////Log.d("myTag", "numConsecutive(calHigh) = "+Integer.toString(numConsecutive));
                            //we update our highest date,
                            ////Log.d("myTag", "calHigh pre add = "+calHigh.toString());
                            //we update our list of dates we are looking for since we had a match
                            for (int k = 0; k < numberOfDays; k++) {
                                calHigh[k].add(Calendar.DATE, numberOfDays);
                            }
                            //since we found a match we want to stop this j loop(the array of dates we are looking for) and start over on a
                            //      fresh i loop(the array of dates that things occured that we are going through)
                            j = numberOfDays;
                            ////Log.d("myTag", "calHigh post add = "+calHigh.toString());
                            //and we reset i to 0 so we start back at the beginning so that no dates are missed,
                            //  this is important because our dates can be listed in any order
                            i = 0;
                        }
                    }//the notes for this 'else if' conditional are the same as above, it is just that it is for the lower end
                    //   of our list of consecutive dates
                }
                if (!alreadyHaveMatch) {
                    for (int j = 0; j < numberOfDays; j++) {
                        if (calLow[j].get(Calendar.YEAR) == calThis.get(Calendar.YEAR) &&
                                calLow[j].get(Calendar.DAY_OF_YEAR) == calThis.get(Calendar.DAY_OF_YEAR)) {

                            calLowOccuranceCount++;

                            if (calLowOccuranceCount == minimumOccurances) {
                                calHighOccuranceCount = 0;
                                calLowOccuranceCount = 0;
                                numConsecutive = numConsecutive + numberOfDays;
                                ////Log.d("myTag", "numConsecutive(calLow) = "+Integer.toString(numConsecutive));
                                ////Log.d("myTag", "calLow[j] pre add = "+calLow[j].toString());
                                ////Log.d("myTag", "calLow[0] pre add = "+calLow[0].toString());
                                ////Log.d("myTag", "calThis pre add = "+calThis.toString());
                                //we update our list of dates we are looking for since we had a match
                                for (int k = 0; k < numberOfDays; k++) {
                                    calLow[k].add(Calendar.DATE, -numberOfDays);
                                }
                                //since we found a match we want to stop this j loop(the array of dates we are looking for) and start over on a
                                //      fresh i loop(the array of dates that things occured that we are going through)
                                j = numberOfDays;
                                ////Log.d("myTag", "calLow post add = "+calLow.toString());
                                i = 0;
                            }
                        }
                    }
                }
            }
            //if we make it out of our for loop, then we want out of the while loop as well, and this boolean being false does
            //  just that
            ////Log.d("myTag", "8");
            continueChecking = false;
        }

        //and we return our counter
        ////Log.d("numConsecutive", Integer.toString(numConsecutive));
        return numConsecutive;
    }

    public int statsGetLongestStreak(String streakType, List<Integer> failureList, List<Integer> successList) {
        int toReturn = 0;

        //this goes through and finds the longest streak of consecutive personal best breaks, it only takes into consideration
        //  endurance runs that ended in a intentionalEnd where it was possible to break a personal best, so initial runs that
        //  have to personal best record before them are not used

        //first we put the list in numeric order. the list that we have here is the number of seconds before right now that a failed
        //  personal break opportunity occured that was not an initial run
        Collections.sort(failureList);
        //now we go through each one of these items

        //if we are just getting the current streak, then we only want to go through this once, from right now(0 seconds ago)
        //  to the last time there was a failure, which will be the first item in our list
        int numberOfCycles = 0;
        if (streakType.equals("current")) {
            numberOfCycles = min(1, failureList.size());
            //but if we are looking for the longest streak then we will go through them all
        } else if (streakType.equals("longest")) {
            numberOfCycles = failureList.size() + 1;

        }


        ////Log.d("myTag", "numberOfCycles/streakType = "+Integer.toString(numberOfCycles)+"/"+streakType);
        for (int i = 0; i < failureList.size(); i++) {
            ////Log.d("myTag", "failureList.get("+Integer.toString(i)+") = "+Integer.toString(failureList.get(i)));
        }

        for (int i = 0; i < successList.size(); i++) {
            ////Log.d("myTag", "successList.get("+Integer.toString(i)+") = "+Integer.toString(successList.get(i)));
        }

        for (int i = 0; i < numberOfCycles; i++) {
            int thisStreaksLength = 0;
            int lowLimit = 0;
            int highLimit = 1000000000;
            if (i > 0) {
                lowLimit = failureList.get(i - 1);
            }
            if (i < failureList.size()) {
                highLimit = failureList.get(i);
            }

            //debugging
            ////Log.d("myTag", "highLimit = "+Integer.toString(highLimit));
            ////Log.d("myTag", "lowLimit = "+Integer.toString(lowLimit));

            //now we see how many personal best breaks ocured between those 2 times
            for (int j = 0; j < successList.size(); j++) {
                if (successList.get(j) > lowLimit &&
                        successList.get(j) < highLimit) {
                    thisStreaksLength++;
                    ////Log.d("myTag", "thisStreaksLength is now "+Integer.toString(thisStreaksLength));
                }
            }
            //if the streak length was higher than our current reigning champ, then we update what the champ is
            if (thisStreaksLength > toReturn) {
                toReturn = thisStreaksLength;
            }
        }

//        if(numberOfCycles == 0){
//            toReturn = successList.size();
//        }

        return toReturn;

    }


    public int[] statsUniqueCombosOfModsAndEntriesWithHistoryInfo() {


        //                  -make 1 function that does it all, it makes 2 string arrays, 1 for endur and 1 for drill, in the arrays
        //                          are just y or n for each string, for endur and drill we just count the ys, for either we use a ||,
        //                          and both we use a &&

        List<String> historyEndurCellsWithData = new ArrayList<>();
        List<String> thisColumn;


        //Array index chart:
        //  [0] = endur
        //  [1] = drill
        //  [2] = either
        //  [3] = both
        //  [4] = endur that ended intentionally
        //  [5] = endur that ended unintentionally
        //  [6] = drill(restart)
        //  [7] = drill(dont restart)
        int[] arrayToReturn = new int[8];
        Arrays.fill(arrayToReturn, 0);

        //we go through all the cells in the HISTORYENDURANCE table, and keep track of whether or not the cells are empty by putting
        //      either a 'y' or an 'n' in the historyEndurCellsWithData list
        for (int i = 2; i < myDb.getFromDB("HISTORYENDURANCE").getColumnCount(); i++) {
            String columnName = myDb.getFromDB("HISTORYENDURANCE").getColumnName(i);

            thisColumn = (getColumnFromDB("HISTORYENDURANCE", columnName));
            for (int j = 0; j < thisColumn.size(); j++) {
                if (thisColumn.get(j) == null || thisColumn.get(j).isEmpty()) {
                    historyEndurCellsWithData.add("n");
                } else if (thisColumn.get(j).equals("")) {
                    historyEndurCellsWithData.add("n");
                } else {
                    historyEndurCellsWithData.add("y");
                    if (thisColumn.get(j).contains("(intentionalEnd)")) {
                        arrayToReturn[4]++;
                    }
                    if (thisColumn.get(j).contains("(unintentionalEnd)")) {
                        arrayToReturn[5]++;
                    }
                    //the size of this counter is only used if 'whatToReturn' = endur
                    arrayToReturn[0]++;
                }
            }
        }


        List<String> historyDrillCellsWithData = new ArrayList<>();


        //we go through all the cells in the HISTORYDRILL table, and keep track of whether or not the cells are empty by putting
        //      either a 'y' or an 'n' in the historyDrillCellsWithData list
        for (int i = 2; i < myDb.getFromDB("HISTORYDRILL").getColumnCount(); i++) {
            String columnName = myDb.getFromDB("HISTORYDRILL").getColumnName(i);

            thisColumn = (getColumnFromDB("HISTORYDRILL", columnName));
            for (int j = 0; j < thisColumn.size(); j++) {
                if (thisColumn.get(j) == null || thisColumn.get(j).isEmpty()) {
                    historyDrillCellsWithData.add("n");
                } else if (thisColumn.get(j).equals("")) {
                    historyDrillCellsWithData.add("n");
                } else {
                    historyDrillCellsWithData.add("y");

                    //Log.d("thisColumn.get(j)", thisColumn.get(j));
                    String[] thisCellSplit = thisColumn.get(j).split("\\)\\(");
                    //the size of this counter is only used if 'whatToReturn' = drill
                    arrayToReturn[1]++;

                    for (int k = 0; k < 4; k++) {


                        if (k % 4 == 0) {
                            //Log.d("thisCellSplit[k]", thisCellSplit[k]);
                        }

                        if (k % 4 == 0 && thisCellSplit[k].equals("(0")) {
                            arrayToReturn[7]++;
                        }
                        if (k % 4 == 0 && !thisCellSplit[k].equals("(0")) {
                            arrayToReturn[6]++;
                        }

                    }
                }

            }
        }
        //it seems to me that the columns and rows for both of our endurance tables are the same, that is why this system of y and n
        //  works to tell which of our unique combos have been used by both drill and endur. This is also why we only need to check
        //  the size of just historyDrillCellsWithData, we can assume that its endur counterpart is the same size

//we go through every item in our arrays and see if at least one of them has a run in it(either),
        //  or if (both) of them have a run in it
        for (int i = 0; i < historyDrillCellsWithData.size(); i++) {

            //if (either) of our arrays are y, then we add 1 to our counter
            if (historyDrillCellsWithData.get(i).equals("y") || historyEndurCellsWithData.get(i).equals("y")) {
                arrayToReturn[2]++;
            }


            //if (both) counters are y, then we add 1 to our counter
            if (historyDrillCellsWithData.get(i).equals("y") && historyEndurCellsWithData.get(i).equals("y")) {
                arrayToReturn[3]++;
            }

        }


        return arrayToReturn;
    }

//checks to see if firstDate is after secondDate
    public boolean isDateAfterDateOrEqual(String firstDate, String secondDate) {
        boolean isAfter = false;

        SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yy HH:mm:ss");
        Date date1 = null;
        Date date2 = null;

        try {
            date1 = formatter.parse(firstDate);
            date2 = formatter.parse(secondDate);

        } catch (ParseException e) {

            e.printStackTrace();
        }
        //Log.d("firstDate", firstDate);
        //Log.d("secondDate", secondDate);
        if (date1.after(date2) || date1.equals(date2)) {
            isAfter = true;

        }


        return isAfter;
    }

    public String addTimeToStringDate(String unitType, int amountToAdd, String DateToAddTo){

        String newDateAsString = "";
        Date newDateAsDate = null;

        SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yy HH:mm:ss");

        try {
            newDateAsDate = formatter.parse(DateToAddTo);//intentionalEnd exception

        } catch (ParseException e) {

            e.printStackTrace();
        }

        Calendar c = Calendar.getInstance();

        if(unitType.equals("SECOND")) {

            c.setTime(newDateAsDate);
            c.add(Calendar.SECOND, amountToAdd);
            newDateAsDate = c.getTime();
        }

        newDateAsString = formatter.format(c.getTime());

        return newDateAsString;
    }

    public int timeSince(String unitType, String pastDate) {

        SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yy HH:mm:ss");
        Date d = null;
        try {
            d = formatter.parse(pastDate);//intentionalEnd exception
        } catch (ParseException e) {

            e.printStackTrace();
        }


        Calendar thatDay = Calendar.getInstance();
        thatDay.setTime(d);

        Calendar today = Calendar.getInstance();

        long diff = today.getTimeInMillis() - thatDay.getTimeInMillis(); //result in millis
        long daysAgo = diff / (24 * 60 * 60 * 1000);
        long minutesAgo = diff / 60000;
        long secondsAgo = diff / 1000;
        int toReturn = 0;
        if (unitType.equals("days")) {
            toReturn = (int) daysAgo;
        }
        if (unitType.equals("seconds")) {
            toReturn = (int) secondsAgo;
        }
        if (unitType.equals("minutes")) {
            toReturn = (int) minutesAgo;
        }
        return toReturn;
    }

    public void setTabColor(TabHost tab) {

        for (int i = 0; i < tab.getTabWidget().getChildCount(); i++) {

            //tab.getTabWidget().getChildAt(i).setBackgroundColor(Color.RED);


            if (i == 0) {
                tab.getTabWidget().getChildAt(i).setBackgroundColor(Color.parseColor("#453F86"));
            }
            if (i == 1) {
                tab.getTabWidget().getChildAt(i).setBackgroundColor(Color.parseColor("#368B56"));
            }
            if (i == 2) {
                tab.getTabWidget().getChildAt(i).setBackgroundColor(Color.parseColor("#C0A54B"));
            }
            if (i == 3) {
                tab.getTabWidget().getChildAt(i).setBackgroundColor(Color.parseColor("#c05F4B"));
            }
            if (i == 4) {
                tab.getTabWidget().getChildAt(i).setBackgroundColor(Color.parseColor("#863F86"));
            }
        }
    }


    //uses whatever inputs were used the last time the app ran to make defaults when app is opened
    public void setDefaultsToSelectionsFromPreviousRun() {


        if(!getCellFromDB("SETTINGS", "MISC", "ID", "6").replaceAll("[^0-9]", "").equals("")) {

            //sets the current number of objects based on what was saved to the settings DB last time a
            //      number was selected
            numOfObjsSpinner.setSelection(Integer.parseInt(getCellFromDB("SETTINGS", "MISC", "ID", "6").replaceAll("[^0-9]", "")));
        }

        if(!getCellFromDB("SETTINGS", "MISC", "ID", "7").replaceAll("[^0-9]", "").equals("")) {


            //sets the current prop type based on what was saved to the settings DB last time an
            //      abject type was selected
            objTypeSpinner.setSelection(Integer.parseInt(getCellFromDB("SETTINGS", "MISC", "ID", "7").replaceAll("[^0-9]", "")));
        }
    }

    public void fillModifierMultiAutocompleteTextViewsFromDB() {

        modifiers.clear(); //first we clear our 'pattern' array
        modifiers.addAll(getColumnFromDB("MODIFIERS", "NAME")); //then we fill it up with what is in the database


        Collections.sort(modifiers, String.CASE_INSENSITIVE_ORDER);


        //using this newly filled array, we create an adapter
        ArrayAdapter<String> patternAdapter = new ArrayAdapter<>(this, android.R.layout.select_dialog_item, modifiers);
        modifierMATV.setAdapter(patternAdapter);//hook the adapter to the ATV

    }

    public void fillPatternMainInputsFromDB() {

        patterns.clear(); //first we clear our 'pattern' array

        //Now we want to get all the ENTRYNAMES from our database
        patterns.addAll(getColumnFromDB("PATTERNS", "ENTRYNAME"));  //they come in this format @@##&&NAME@@##&&NUM@@##&&TYPE@@##&&

        Collections.sort(patterns, String.CASE_INSENSITIVE_ORDER);
        //Log.d("lots", "6");
        //fillObjectTypeATVsfromDB();

        for (int i = 0; i < patterns.size(); i++) {
            //we ony want the ones that have the same NUM and TYPE as is currently selected in our main input, so
            //      if the statement "this patterns string contains both the (prop number) and (prop type)" is true,
            //          then we are going to use it
            if (patterns.get(i).contains(buffer + numOfObjsSpinner.getSelectedItem().toString() + buffer) &&
                    patterns.get(i).contains(buffer + objTypeSpinner.getSelectedItem().toString() + buffer)) {

                //all we want from this string is the name which is in the first set of parenthesis
                //      so we split the string on "@@##&&"
                //      EXAMPLE: @@##&&cascade@@##&&5@@##&&balls@@##&& BECOMES: "cascade" "5" "balls"
                String[] thisPattern = patterns.get(i).split(buffer);
                //then we take the first item we split off, in our example, it is "cascade"

//                //      and we use substring(1) to remove the first character from it to get "cascade"
//                patterns.set(i,thisPattern[0].substring(1));

                //      and we set pattern(i) to the first split string, "cascade"
                patterns.set(i, thisPattern[1]);

                //debugging
                //Toast.makeText(getBaseContext(), "name = "+patterns.get(i), Toast.LENGTH_LONG).show();
            } else { //and if this string didn't match the (num) & (type) requirements, then we just get rid of it
                patterns.remove(i);
                i--; //we need to subtract 1 from 'i' because when we remove an item from our list, all the items after it move down 1
                //          to fill the gap and we would skip the next item
            }
        }

        //if the pattern currently in the patternATV isn't in our, now modified, patterns array, then set the text to blank
        if (!patterns.contains(patternsATV.getText().toString())) {
            patternsATV.setText("");
        }


        //using this filtered array, we create an adapter
        ArrayAdapter<String> patternAdapter = new ArrayAdapter<>(this, android.R.layout.select_dialog_item, patterns);
        //populate the patternsATV unintentionalEnddown with the patterns array
        patternsATV.setAdapter(patternAdapter);

        //this fills the lower modifiers ATV with whatever is currently selected in the upper modifiers MATV, at the
        //      start of the app it is blank, so it fills it with nothing.
        updateAddModifierFromModifierMATVselected();
    }

    //--------------------------END DIRECTLY USED BY onCreate()----------------------------------


    //--------------------------BEGIN MAIN INPUT FUNCTIONS----------------------------------
    public void fillObjectTypeATVsfromDB() {
        //first we have to get the string from our datatase
        //String[] str =  getCellFromDB("SETTINGS","MISC","2").split(",");
        //Toast.makeText(MainActivity.this, "weAreHere", Toast.LENGTH_LONG).show();
        //Log.d("lots of this", "2");
        setObjectTypeLists();

        //String[] addPatObjTypeForSpinnerX =  getCellFromDB("SETTINGS","MISC","ID","2").split(",");
        //then we apply that array to our ATV

        //using this newly filled array, we create an adapter
        ArrayAdapter<String> objectTypeAdapter = new ArrayAdapter<>(this, android.R.layout.select_dialog_item, addPatObjTypeForSpinner);
        //settingsObjectTypeATV.setAdapter(objectTypeAdapter);//hook the adapter to the ATV
        objTypeSpinner.setAdapter(objectTypeAdapter);
    }


    public void setObjectTypeLists() {
        //Log.d("myTag", "lots of this?");
        addPatObjTypeForSpinner = Arrays.asList(getCellFromDB("SETTINGS", "MISC", "ID", "2").split(","));
        //now we alphabetize the list
        Collections.sort(addPatObjTypeForSpinner, String.CASE_INSENSITIVE_ORDER);

        addPatObjTypeForMATV.clear();
        //we want 'select all' to be first, and the reset of the list in alphabetical order afterwards
        addPatObjTypeForMATV.add("Select all");
        List temp = Arrays.asList(getCellFromDB("SETTINGS", "MISC", "ID", "2").split(","));
        //now we alphabetize the list
        Collections.sort(temp, String.CASE_INSENSITIVE_ORDER);
        addPatObjTypeForMATV.addAll(temp);

    }

    public void setSpecialThrowSequenceList() {
        setSequenceForSpinner = Arrays.asList(getCellFromDB("SETTINGS", "MISC", "ID", "10").split(","));
        //now we alphabetize the list
        Collections.sort(setSequenceForSpinner, String.CASE_INSENSITIVE_ORDER);

    }

    public void updateModifierMATV() {


        //Toast.makeText(MainActivity.this, "stopper", Toast.LENGTH_LONG).show();


        //we are only going to do any of this if there actually is anything in the ModifiersATV

        if (modifierMATV.getText().toString().trim().length() > 0) {

            //before we do any checks, we want to clean up our commas by replacing all instances of comma+whitespace with just commas
            modifierMATV.setText(modifierMATV.getText().toString().replace(", ", ","));

            //and then replacing all commas with commas+one whitespace
            modifierMATV.setText(modifierMATV.getText().toString().replace(",", ", "));

            //so, now all the modifiers are separated by exactly one comma and one whitespace


            //before we try to fill anything in any tabs from our ATVs, we need to make sure that the text in our ATVs represents something
            //      that is actually in our database

            //we split the modifierMATV string into an array
            List<String> listOfSplitModifiersString = new ArrayList<>();
            Collections.addAll(listOfSplitModifiersString, modifierMATV.getEditableText().toString().split(", "));

            //Collections.sort(listOfSplitModifiersString, String.CASE_INSENSITIVE_ORDER);


            //we check every string in that array up against the modifiers we have in our DB
            for (int i = 0; i < listOfSplitModifiersString.size(); i++) {

//              if we find something not in the DB..
                if (!containsCaseInsensitive(listOfSplitModifiersString.get(i), getColumnFromDB("MODIFIERS", "NAME"))) {
                    //...it gets removed

                    //Toast.makeText(MainActivity.this, "not in db", Toast.LENGTH_LONG).show();
                    listOfSplitModifiersString.remove(i);
                    i--; //when we remove something from our list all the other index numbers get reduced by 1 to compensate,
                    //              so we need to bring the index number down 1 to make up for it so nothing gets ignored
                }
            }

            //we want to make sure that we even have a modifier in here at this point before we proceed,
            //          if nothing in the modifierMATV was in the DB, then we have just deleted everything
            if (listOfSplitModifiersString.size() > 0) {

                //go through each string in the array
                for (int j = 0; j < listOfSplitModifiersString.size(); j++) {

                    //we are going to compare each string in the list to each string in the list,
                    //      so 1 duplicate for each string is what we would expect if there was no duplicates,
                    //      before we go through the list for each string we set the number of duplicates to 0
                    int numberOfDuplicates = 0;
                    //this is where we go through the list again for each string
                    for (int k = 0; k < listOfSplitModifiersString.size(); k++) {
                        //if we find a duplicate..
                        if (listOfSplitModifiersString.get(j).equals(listOfSplitModifiersString.get(k))) {
                            //..we add 1 to our counter
                            numberOfDuplicates++;
                            //if our counter gets higher than 1
                            if (numberOfDuplicates > 1) {
                                //then we remove the duplicate
                                listOfSplitModifiersString.remove(k);
                                //the indices of the other numbers in the list get reasigned to make up for the missing
                                //      string, so we need to reduce 'k' by one so that another string doesn't get skipped
                                k--;

                            }
                        }
                    }
                }

            } else {//if we currently have nothing in the list, then we go ahead and add a blank string,
                //      but this probably should never actually happen since we are only in here if we do have at least something in the list

                listOfSplitModifiersString.add("");
            }

            //now that we have this list with the duplicates and the 'not in our DB' strings removed,
            //      we must use this list to make a new string which will be put into our modifierMATV

            modifierMATV.setText((android.text.TextUtils.join(", ", listOfSplitModifiersString)) + ", ");
            if (modifierMATV.getEditableText().toString().equals(", ")) {
                modifierMATV.setText("");
            }

            //this just moves the cursor to the end of modifierMATV
            modifierMATV.setSelection(modifierMATV.getText().length());


            //Toast.makeText(MainActivity.this, listOfSplitModifiersString.get(listOfSplitModifiersString.size() - 1), Toast.LENGTH_LONG).show();


        }
    }

    //this gets rid of any duplicates, or anything that isn't actually in the DB for this MATV
    public void updateMATV(MultiAutoCompleteTextView MATV, String fromDB) {


        //Toast.makeText(MainActivity.this, "stopper", Toast.LENGTH_LONG).show();


        //we are only going to do any of this if there actually is anything in the MATV

        if (MATV.getText().toString().trim().length() > 0) {

            //before we do any checks, we want to clean up our commas by replacing all instances of comma+whitespace with just commas
            MATV.setText(MATV.getText().toString().replace(", ", ","));

            //and then replacing all commas with commas+one whitespace
            MATV.setText(MATV.getText().toString().replace(",", ", "));

            //so, now all the items are separated by exactly one comma and one whitespace


            //we need to make sure that the text in our MATV represents something
            //      that is actually in our database

            //we split the MATV string into an array
            List<String> listOfSplitItemsString = new ArrayList<>();
            Collections.addAll(listOfSplitItemsString, MATV.getEditableText().toString().split(", "));


            //Collections.sort(listOfSplitItemsString, String.CASE_INSENSITIVE_ORDER);


            //we check every string in that array up against the modifiers we have in our DB
            for (int i = 0; i < listOfSplitItemsString.size(); i++) {

//              if we find something not in the DB..
                if (!containsCaseInsensitive(listOfSplitItemsString.get(i), Arrays.asList(fromDB.split(",")))) {
                    //...it gets removed

                    //debugging
                    //Toast.makeText(MainActivity.this, listOfSplitItemsString.get(i)+" not in db", Toast.LENGTH_LONG).show();

                    listOfSplitItemsString.remove(i);

                    //WE MIGHT NEED THIS i-- :
                    //          -it would probably only be used for times that a item that is not in the DB gets added
                    //                  somewhere into the middle of the addPatternAttributesMATV since it is irrelevant at
                    //                  at the end of the String
                    //  BTW, IT WOULD ALSO NEED TO BE ADDED INTO 'updateModifierMATV()'
                    i--;
                }
            }


            //we want to make sure that we even have a item in here at this point before we proceed,
            //          if nothing in the addPatternItemsMATV was in the DB, then we have just deleted everything
            if (listOfSplitItemsString.size() > 0) {

                //go through each string in the array
                for (int j = 0; j < listOfSplitItemsString.size(); j++) {

                    //we are going to compare each string in the list to each string in the list,
                    //      so 1 duplicate for each string is what we would expect if there was no duplicates,
                    //      before we go through the list for each string we set the number of duplicates to 0
                    int numberOfDuplicates = 0;
                    //this is where we go through the list again for each string
                    for (int k = 0; k < listOfSplitItemsString.size(); k++) {
                        //if we find a duplicate..
                        if (listOfSplitItemsString.get(j).equals(listOfSplitItemsString.get(k))) {
                            //..we add 1 to our counter
                            numberOfDuplicates++;
                            //if our counter gets higher than 1
                            if (numberOfDuplicates > 1) {
                                //then we remove the duplicate
                                listOfSplitItemsString.remove(k);
                                //the indices of the other numbers in the list get reassigned to make up for the missing
                                //      string, so we need to reduce 'k' by one so that another string doesn't get skipped
                                k--;

                            }
                        }
                    }
                }

            } else {//if we currently have nothing in the list, then we go ahead and add a blank string,
                //      but this probably should never actually happen since we are only in here if we
                // //       do have at least something in the list

                listOfSplitItemsString.add("");
            }


            //now that we have this list with the duplicates and the 'not in our DB' strings removed,
            //      we must use this list to make a new string which will be put into our modifierMATV

            MATV.setText((android.text.TextUtils.join(", ", listOfSplitItemsString)) + ", ");
            if (MATV.getEditableText().toString().equals(", ")) {
                MATV.setText("");
            }

            //this just moves the cursor to the end of modifierMATV
            MATV.setSelection(MATV.getText().length());


        }
    }
    //--------------------------END MAIN INPUT FUNCTIONS----------------------------------

    //--------------------------BEGIN RUN TAB ACTIVITY FUNCTIONS--------------------------------
    public void addModifiersToHistoryTablesIfDoesntExist() {

        //we check to see if the currently selected combination of modifiers has a row in the HISTORY tables

        //in order to do this, we need to get the currently selected modifiers in one string,
        //      seperated by commas, and in alphebetical order and check to see if a cell in
        //     our lists of modifiers exists with this exact string in it,
        //          if it doesn't already exist, then we add it

        if (!containsCaseInsensitive(stringOfCurrentlySelectedModifiersInAlphabeticalOrder(),
                getColumnFromDB("HISTORYDRILL", "MODIFIERS"))) {//btw, we can just check HISTORYDRILL and know that
            //                                          HISTORYENDURANCE matches because we always add/remove rows to them together


            addDataToDB("HISTORYDRILL", "MODIFIERS",
                    stringOfCurrentlySelectedModifiersInAlphabeticalOrder());
            addDataToDB("HISTORYENDURANCE", "MODIFIERS",
                    stringOfCurrentlySelectedModifiersInAlphabeticalOrder());
        }
    }

    public void addPatternToHistoryTablesIfDoesntExist() {

        //we check to see if the currently selected combination of modifiers has a row in the HISTORY tables

        //in order to do this, we need to get the currently selected modifiers in one string,
        //      seperated by commas, and in alphebetical order and check to see if a cell in
        //     our lists of modifiers exists with this exact string in it,
        //          if it doesn't already exist, then we add it

        if (!myDb.existsColumnInTable("HISTORYDRILL", currentEntryName())) {//btw, we can just check HISTORYDRILL and know that
            //                                          HISTORYENDURANCE matches because we always add/remove rows to them together

            //and we add it to both of our 'history' tables
            myDb.addColumn("HISTORYDRILL", currentEntryName(), "TEXT");
            myDb.addColumn("HISTORYENDURANCE", currentEntryName(), "TEXT");

            //we need to close and establish a connection with the databse because we have just added columns
            myDb.close();
            myDb = new DatabaseHelper(this);
        }
    }

    public void addRunResultToHistoryDrillTableInDB() {

        //since we are adding a run to the history, we want to start a session if we are not currently in one, or if the last
        //      session we started was more than 10 minutes ago.
        runStartSessionIfNeeded();

        //if this is the first time the user has done a run with this combination of modifiers,
        //      then it wouldn't be in the HISTORY tables, in which case, we add them
        addModifiersToHistoryTablesIfDoesntExist();

        //and we do the same thing for the pattern being used
        addPatternToHistoryTablesIfDoesntExist();

        //now we go ahead and add the run results to the past run results that may or may not
        // already exist at the cell located at the intersection of the currently selected pattern and the modifiers

        //first we need to get the cell from the table that matches the run type we are in and the
        //      relevant pattern/modifiers


        String specificsFromCompletedRun = "";

//this might be useful!!!!!
        //-the format for drills is (#ofAttempts,extra number of seconds due to failed attempts-if 'restart', otherwise just make it a (0))(#ofSets)(setLength)(date/time),

        //if runDrillAttemptCounter = 0, then we know we are not using fatal drops, if it is 1 or higher than we are
        //      and it is the accurate number of attempts
        //The reason we use 'addTimeToStringDate' is because we want to get the time that the run began which is the
        //      current time minus the length of the run
        if (runDrillAttemptCounter == 0) {
            specificsFromCompletedRun = "(" + runDrillAttemptCounter + ")(" + runNumberPickerSets.getValue() + ")(" +
                    totalNumberOfDrillSeconds() + ")(" + addTimeToStringDate("SECOND", (-1*totalNumberOfDrillSeconds()), currentDateAndTime()) + ")";
        } else {//since we are doing a 'restart set on unintentionalEnds' run, we also need to keep track of the amount of time spent on failed attempts, we do that by putting a comma
            //      after the runDrillAttemptCounter, and the amount of time spent on failed attempts (in seconds) after the comma
            specificsFromCompletedRun = "(" + runDrillAttemptCounter + "," + runDrillTimeSpentOnFailedAttempts + ")(" + runNumberPickerSets.getValue() + ")(" +
                    totalNumberOfDrillSeconds() + ")(" + addTimeToStringDate("SECOND", (-1*totalNumberOfDrillSeconds()), currentDateAndTime())  + ")";

        }


//we want to add on to what is currently in the relevant cell of the DB
        String currentlyInCellToBeUpdated = getCellFromDB
                ("HISTORYDRILL", currentEntryName(), "MODIFIERS", stringOfCurrentlySelectedModifiersInAlphabeticalOrder());

        //this is just for debugging, it is everything that is currently in the cell
        //Toast.makeText(MainActivity.this, currentlyInCellToBeUpdated, Toast.LENGTH_LONG).show();

        //if there is currently nothing in the cell, then we don't want to try to add on to it
        String stringToUpdateDBwith = "";
        if (currentlyInCellToBeUpdated == null) {
            stringToUpdateDBwith = specificsFromCompletedRun;
        } else {
            stringToUpdateDBwith = currentlyInCellToBeUpdated + specificsFromCompletedRun;
        }

        updateDataInDB("HISTORYDRILL", currentEntryName(), "MODIFIERS",
                stringOfCurrentlySelectedModifiersInAlphabeticalOrder(), stringToUpdateDBwith);

        fillHistoryRecordsTextView();

    }

    public void addRunResultToHistoryEnduranceTableInDB(String endType, String endExplanation) {

        //if they ended the endurance attempt before it even began, show this toast
        if (runsEnduranceTimerCurrentTime == 0) {

            //debugging
            //Toast.makeText(MainActivity.this,"Endurance attempt canceled.", Toast.LENGTH_LONG).show();

        } else {//but if they actually got some time on the timer, then we add this run add to the DB

            //since we are adding a run to the history, we want to start a session if we are not currently in one, or if the last
            //      session we started was more than 10 minutes ago.
            runStartSessionIfNeeded();

            //first, show them what they did
            Toast.makeText(MainActivity.this, "Ended at " + formattedTime(runsEnduranceTimerCurrentTime) +
                    " with a " + endType + ".", Toast.LENGTH_LONG).show();

            //if this is the first time the user has done a run with this combination of modifiers,
            //      then it wouldn't be in the HISTORY tables, in which case, we add them
            addModifiersToHistoryTablesIfDoesntExist();

            //and we do the same thing for the pattern being used
            addPatternToHistoryTablesIfDoesntExist();


            //now we go ahead and add the run results to the past run results that may or may not
            // already exist at the cell located at the intersection of the currently selected pattern and the modifiers

            //first we need to get the cell from the table that matches the run type we are in and the
            //      relevant pattern/modifiers

            //-the format for endurance entries is: (end explanation)(intentionalEnd/unintentionalEnd)(run length)(date/time)
            //The reason we use 'addTimeToStringDate' is because we want to get the time that the run began which is the
            //      current time minus the length of the run
            String specificsFromCompletedRun =
                    "(" + endExplanation + ")(" + endType + ")(" + Integer.toString(runsEnduranceTimerCurrentTime) +
                            ")(" + addTimeToStringDate("SECOND", (-1*runsEnduranceTimerCurrentTime), currentDateAndTime()) + ")";

            //we want to add on to what is currently in the relevant cell of the DB
            String currentlyInCellToBeUpdated = getCellFromDB
                    ("HISTORYENDURANCE", currentEntryName(), "MODIFIERS", stringOfCurrentlySelectedModifiersInAlphabeticalOrder());

            //this is just for debugging, it is everything that is currently in the cell
            //Toast.makeText(MainActivity.this, currentlyInCellToBeUpdated, Toast.LENGTH_LONG).show();

            //if there is currently nothing in the cell, then we don't want to try to add on to it
            String stringToUpdateDBwith = "";
            if (currentlyInCellToBeUpdated == null) {
                stringToUpdateDBwith = specificsFromCompletedRun;
            } else {
                stringToUpdateDBwith = currentlyInCellToBeUpdated + specificsFromCompletedRun;
            }

            updateDataInDB("HISTORYENDURANCE", currentEntryName(), "MODIFIERS",
                    stringOfCurrentlySelectedModifiersInAlphabeticalOrder(), stringToUpdateDBwith);

            fillHistoryRecordsTextView();


        }
    }

    public String currentDateAndTime() {


        Calendar c = Calendar.getInstance();
        System.out.println("Current time => " + c.getTime());

        SimpleDateFormat df = new SimpleDateFormat("MM/dd/yy HH:mm:ss");
        String formattedDateAndTime = df.format(c.getTime());
        // formattedDate have current date/time

        return formattedDateAndTime;
    }

    public String stringOfCurrentlySelectedModifiersInAlphabeticalOrder() {

        if (modifierMATV.length() > 0) { //we only want to try to alphabetize if there are actually modifiers

            String currentlySelectedModifiersInAlphabeticalOrder = "";

            //currently the modifiers are in one string, we need to take that string and turn it into a list
            //we split the modifierMATV string into an array
            List<String> listOfSplitModifiersString = new ArrayList<>();
            Collections.addAll(listOfSplitModifiersString, modifierMATV.getEditableText().toString().split(", "));

            //now we alphabetize the list
            Collections.sort(listOfSplitModifiersString, String.CASE_INSENSITIVE_ORDER);

            currentlySelectedModifiersInAlphabeticalOrder = ((android.text.TextUtils.join(", ", listOfSplitModifiersString)) + ", ");

            return currentlySelectedModifiersInAlphabeticalOrder;

        } else {//if there are no modifiers, then we return a blank string

            return "";
        }
    }

    public int totalNumberOfDrillSeconds() {

        return runNumberPickerHours.getValue() * 3600 +
                runNumberPickerMinutes.getValue() * 60 +
                runNumberPickerSeconds.getValue();
    }

    public String formattedTime(int totalSeconds) {

        int hours = totalSeconds / 3600;
        int minutes = (totalSeconds % 3600) / 60;
        int seconds = totalSeconds % 60;

        String time = String.format("%02d:%02d:%02d", hours, minutes, seconds);

        return time;
    }
    
    public int secondsFromFormattedTime(String formattedTime){
        int seconds = 0;
        Log.d("formattedTime", formattedTime);
        //formattedTime.replaceAll("\\n", "");
        formattedTime = formattedTime.trim();
        String[] formattedTimeSplit = formattedTime.split(":");
        for (int i = 0; i<3;i++){
            if (formattedTimeSplit[i].contains("00")){
                formattedTimeSplit[i] = "0";
            }
        }
        seconds = (Integer.parseInt(formattedTimeSplit[0])*3600)+
                (Integer.parseInt(formattedTimeSplit[1])*60)+
                (Integer.parseInt(formattedTimeSplit[2]));
        return seconds;
    }

    public String formattedTimeWithDays(int totalSeconds) {

        int days = totalSeconds / 86400;
        int hours = (totalSeconds % 86400) / 3600;
        int minutes = (totalSeconds % 3600) / 60;
        int seconds = totalSeconds % 60;


        String time = String.format("%03d:%02d:%02d:%02d", days, hours, minutes, seconds);

        return time;
    }
    //--------------------------END RUN TAB ACTIVITY FUNCTIONS----------------------------------

    //--------------------------BEGIN HISTORY TAB ACTIVITY FUNCTIONS----------------------------------

    public String[] arrayOfRecordsBasedOnHistorySettings(String recordType) {

        //this list will be filled with the raw data from our database and then filtered out based on
        //      whether we want 'unintentionalEnd' 'intentionalEnd' or 'both' endings(endurance) or 'restart on unintentionalEnd'(drill).
        //      After all that it will be converted to a string[] and returned to whatever function requested it.


        List<String> listOfDatabaseItems = new ArrayList<>();
        Collections.addAll(listOfDatabaseItems, (getCellFromDB(recordType, currentEntryName(),
                "MODIFIERS", stringOfCurrentlySelectedModifiersInAlphabeticalOrder()).split("\\)\\(")));


        //String mine = android.text.TextUtils.join(",",listOfDatabaseItems);

        //for debugging
        //Toast.makeText(MainActivity.this, Integer.toString(listOfDatabaseItems.size()), Toast.LENGTH_LONG).show();


        //now that we have our list, we need to clean up the first and last item on it because the first item will have
        //      an '(' and the last a ')'

        //this sets the first item as itself without the '('
        listOfDatabaseItems.set(0, listOfDatabaseItems.get(0).substring(1));

        //then we use the length of the array to get the index number of the final string
        int finalIndex = listOfDatabaseItems.size() - 1;

        //and we use that index to get our final string and we remove the ')' from the end of it
        listOfDatabaseItems.set(finalIndex, listOfDatabaseItems.get(finalIndex).substring(0,
                listOfDatabaseItems.get(finalIndex).length() - 1));

        //now we have a clean list of strings, each string being a bit of add on an entry in our history records

        //if we are in HISTORYENDURANCE, we want to rid this list of all entries that are unwanted
        // based on whether we want to remove 'unintentionalEnd', 'intentionalEnd', or 'none'
        if (recordType.equals("HISTORYENDURANCE")) {

            //      if we want 'both' then we are already done and don't need to do anything to our list,
            //      but if we want 'intentionalEnd' or 'unintentionalEnd' then we need to weed stuff out,

            //by default we will make this none(for both), and only change it if we must
            String endTypeToRemove = "none";

//            //if intentionalEnd is checked, then we want to remove unintentionalEnd
//            if (historyRadioButtonIntentionalEnd.isChecked()) {
//                endTypeToRemove = "unintentionalEnd";
//            }
//
//            //if unintentionalEnd is checked, then we want to remove intentionalEnd
//            if (historyRadioButtonUnintentionalEnd.isChecked()) {
//                endTypeToRemove = "intentionalEnd";
//            }

            //Toast.makeText(MainActivity.this, Integer.toString(listOfDatabaseItems.size()), Toast.LENGTH_LONG).show();


            //if it doesn't equal 'none', then we know have some weeding to do
            if (!endTypeToRemove.equals("none")) {

                //so, we need to go through each string in 'listOfDatabaseItems',
                Log.d("lots", "here");
                for (int i = 0; i < listOfDatabaseItems.size() / 4; i++) {
                    //if we find one that has our 'endTypeToRemove' then..
                    if (listOfDatabaseItems.get(i * 4 + 1).equals(endTypeToRemove)) {
                        //      ..we remove them and the three following strings since they are apart
                        //      of the same run entry because 'HISTORYENDURANCE' items are in
                        //      this format: (explanation)(intentionalEnd/unintentionalEnd)(run length)(date/time),
                        listOfDatabaseItems.remove(i * 4);
                        listOfDatabaseItems.remove(i * 4);
                        listOfDatabaseItems.remove(i * 4);
                        listOfDatabaseItems.remove(i * 4);

                        //and then since the following items will fill in the empty space, we must
                        //  lower i by 1 so we don't miss anything. this is also why we remove the same index 3 times above.
                        i--;
                        //                       Toast.makeText(MainActivity.this, Integer.toString(i), Toast.LENGTH_LONG).show();

                    }
                }


            }
        }//this is the end of the removal of items based on whether we are using unintentionalEnd/intentionalEnd/both


        else {//and this begins the check we do if we are in HISTORYDRILL,
            //  the filter we need to do here is for whether or not we are using 'restart set on unintentionalEnds'

            //if we are using it, then we only want the entries where #ofAttempts > 0
            if (historyDrillRestartSetOnUnintentionalEnds.isChecked()) {
                //so, we need to go through each string in 'listOfDatabaseItems',
                for (int i = 0; i < listOfDatabaseItems.size() / 4; i++) {
                    //if we find one that has a '0'..
                    if (listOfDatabaseItems.get(i * 4).equals("0")) {
                        //      ..then we remove it and the three following strings since they are apart
                        //      of the same run entry because 'HISTORYDRILL' items are in
                        //      this format:
                        // (#ofAttempts,extra number of seconds due to failed attempts-if 'restart', otherwise just make it a (0)))(#ofSets)(setLength)(date/time),

                        listOfDatabaseItems.remove(i * 4);
                        listOfDatabaseItems.remove(i * 4);
                        listOfDatabaseItems.remove(i * 4);
                        listOfDatabaseItems.remove(i * 4);

                        //and then since the following items will fill in the empty space, we must
                        //  lower i by 1 so we don't miss anything. this is also why we remove the same index 4 times above.
                        i--;
                    }
                }
            } else {//if we are not using 'restart set on unintentionalEnds', then we only want entries where #ofAttempts is 0
                //so, we need to go through each string in 'listOfDatabaseItems',
                for (int i = 0; i < listOfDatabaseItems.size() / 4; i++) {
                    //if we find one that doesn't have a '0'..
                    if (!listOfDatabaseItems.get(i * 4).equals("0")) {
                        //      ..then we remove it and the three following strings since they are apart
                        //      of the same run entry because 'HISTORYDRILL' items are in
                        //      this format:
                        // (#ofAttempts-if fatal drops, otherwise just make it a (0))(#ofSets)(setLength)(date/time),

                        listOfDatabaseItems.remove(i * 4);
                        listOfDatabaseItems.remove(i * 4);
                        listOfDatabaseItems.remove(i * 4);
                        listOfDatabaseItems.remove(i * 4);

                        //and then since the following items will fill in the empty space, we must
                        //  lower i by 1 so we don't miss anything. this is also why we remove the same index 4 times above.
                        i--;
                    }
                }
            }

        }

        //at this point we should have our list and it is filtered out based on
        //  unintentionalEnd/intentionalEnd/both if we are in HISTORYENDURANCE
        //  and
        //  'restart set on unintentionalEnds' if we are in HISTORYDRILL

        //now we convert the list to a string[] since we are no longer going to need to change it's size
        String[] arrayOfDatabaseItems = new String[listOfDatabaseItems.size()];
        listOfDatabaseItems.toArray(arrayOfDatabaseItems);


        return arrayOfDatabaseItems;
    }
//
//    //this function is run whenever an option changes in the history tab activity
//    //  and whenever a pattern or modifier change occurs in the main inputs
//    public void loadHistoryRecordsFromDB() {
//
//        if (patternsATV.length() < 1) {
//
//            historyRecordsTextView.setText("No pattern name selected.");
//
//        } else {
//
//            if (!containsCaseInsensitive(stringOfCurrentlySelectedModifiersInAlphabeticalOrder(),
//                    getColumnFromDB("HISTORYDRILL", "MODIFIERS")) ||
//                    !myDb.existsColumnInTable("HISTORYDRILL", currentEntryName())) {
//                historyRecordsTextView.setText("No records exist.");
//
//            } else {
//
//                //just for debugging
//                //Toast.makeText(MainActivity.this,"made it here 1", Toast.LENGTH_LONG).show();
//
//                //we will do a 'historyRecordsTextView.setText' in both of the two below functions based on what
//                //      settings are set
//                if (historyRadioButtonEndurance.isChecked()) {
//                    loadEnduranceHistoryRecordsFromDB();
//
//                } else {//if endurance isn't checked, then drill must be
//                    loadDrillHistoryRecordsFromDB();
//
//                }
//
//
//            }
//        }
//        histObjectTypesOtherThanCurrentCheckbox.setText("Object types other than '"+objTypeSpinner.getSelectedItem().toString()+"'");
//        histNumberOfObjectsOtherThanCurrentCheckbox.setText("Number of objects other than '"+numOfObjsSpinner.getSelectedItem().toString()+"'");
//        histPatternNamesOtherThanCurrentCheckbox.setText("Pattern names other than '"+patternsATV.getText().toString()+"'");
//        histModifierCombinationOtherThanExactlyCurrentCheckbox.setText("Modifiers other than '"+modifierMATV.getText().toString()+"'");
//    }
//
//    public void loadDrillHistoryRecordsFromDB() {
//        //if the cell that exists at the intersection of the pattern and modifier combo is null, then there are no
//        //      records and addrm the user of that and that is it...
//        if (getCellFromDB("HISTORYDRILL", currentEntryName(), "MODIFIERS",
//                stringOfCurrentlySelectedModifiersInAlphabeticalOrder()) == null ||
//                getCellFromDB("HISTORYDRILL", currentEntryName(), "MODIFIERS",
//                        stringOfCurrentlySelectedModifiersInAlphabeticalOrder()).equals("")) {
//            historyRecordsTextView.setText("No drill records exist.");
//        } else {//but if it isn't null, then we need to display them based on the filter created by the radiobuttons and checkbox
//
//            //we have two different ways we want to display the records,
//            //  1)'restart set on unintentionalEnds' runs
//            //  2)don't 'restart set on unintentionalEnds' run
//            //only the entries that are relevant will be coming from arrayOfRecordsBasedOnHistorySettings("HISTORYDRILL");
//            //      but the display of them is different for either because we want to only print
//            //          #of attempts if we are doing 'restart set on unintentionalEnds', otherwise it is just a 0 and we ignore it.
//
//            //either way we want this stuff:
//
//            //
//            String[] allRecordStrings = arrayOfRecordsBasedOnHistorySettings("HISTORYDRILL");
//
//            int finalIndex = allRecordStrings.length - 1;
//
//            //we will fill this up later
//            String textForTextView = "";
//
//            //if we are doing 'restart set on unintentionalEnds' runs
//            if (historyDrillRestartSetOnUnintentionalEnds.isChecked()) {
//                for (int i = finalIndex; i > -1; i--) {
//
//                    // (#ofAttempts,extra nunber of seconds due to failed attempts-if 'restart', otherwise just make it a (0)))(#ofSets)(setLength)(date/time),
//
//
//                    //since everything has been input in the same pattern, we can determine what is what by getting
//                    //  modulus of everything, and we will immedietly put everything right into our string 'textForTextView'
//
//                    //this is a date/time
//                    if (i % 4 == 3) {
//                        textForTextView = textForTextView + allRecordStrings[i] + "\n";
//                    }
//
//                    //this is a run length
//                    if (i % 4 == 2) {
//                        textForTextView = textForTextView + "Set length: " + formattedTime(Integer.parseInt(allRecordStrings[i]
//                                .replaceAll("[^0-9]", ""))) + "\n";
//                    }
//
//                    //this is the number of sets
//                    if (i % 4 == 1) {
//                        textForTextView = textForTextView + "Sets: " + allRecordStrings[i] + "\n";
//                    }
//
//                    //this is the number of attempts
//                    if (i % 4 == 0) {
//
//                        textForTextView = textForTextView + "Attempts: " + allRecordStrings[i].split(",")[0] + "\n";
//                        if (allRecordStrings[i].contains(",")) {
//                            textForTextView = textForTextView + "Extra: " + formattedTime(Integer.parseInt(allRecordStrings[i].split(",")[1].replaceAll("[^0-9]", ""))) + "\n\n";
//                        } else {
//                            textForTextView = textForTextView + "\n";
//                        }
//
//                    }
//                }
//            } else {//if we are not doing 'restart set on unintentionalEnds' runs
//                for (int i = finalIndex; i > -1; i--) {
//
//                    //since everything has been input in the same pattern, we can determine what is what by getting
//                    //  modulus of everything, and we will immediately put everything right into our string 'textForTextView'
//
//                    //this is a date/time
//                    if (i % 4 == 3) {
//                        textForTextView = textForTextView + allRecordStrings[i] + "\n";
//                    }
//                    //this is a run length
//                    if (i % 4 == 2) {
//                        textForTextView = textForTextView + "Length: " + formattedTime(Integer.parseInt(allRecordStrings[i]
//                                .replaceAll("[^0-9]", ""))) + "\n";
//                    }
//
//                    //this is the number of sets
//                    if (i % 4 == 1) {
//                        textForTextView = textForTextView + "Sets: " + allRecordStrings[i] + "\n\n";
//                    }
//
//
//                }
//
//            }
//            //this is where we actualy show the text that we have built
//            historyRecordsTextView.setText(textForTextView);
//        }
//    }

    public int getPersonalBest(String endType) {
        //we want to go ahead and run 'loadEnduranceHistoryRecordsFromDB()' with both selected and then we can just
        //      pick bth personal bests out of there.

        //so, first we want to find out what is currently selected ove in the history tab activity, both, intentionalEnd, or unintentionalEnd
 //       String currentlyChecked = "both";

//        if (historyRadioButtonIntentionalEnd.isChecked()) {
//            currentlyChecked = "intentionalEnd";
//        }
//
//        if (historyRadioButtonUnintentionalEnd.isChecked()) {
//            currentlyChecked = "unintentionalEnd";
//        }
//
//        //now we have a record of what is currently selected and we are safe to go ahead and change it to 'both'
//        historyRadioButtonBoth.setChecked(true);

        //changing the selected radioButton makes loadEnduranceHistoryRecordsFromDB() run automatically,
        //      so now we just access the current 'personal bests' that get figured and stored in global variables,
        //      currentIntentionalEndPersonalBest and currentUnintentionalEndPersonalBest

        int personalBestToReturn = 0;

        //depending on what is being requested, we fill our int, personalBestToReturn, which will be returned
        if (endType.equals("intentionalEnd")) {
            personalBestToReturn = currentIntentionalEndPersonalBest;
        }

        if (endType.equals("unintentionalEnd")) {
            personalBestToReturn = currentUnintentionalEndPersonalBest;
        }

//        //now that we have personalBestToReturn filled, we check whichever one was checked when we got here so that it
//        //      is how it was originally
//        if (currentlyChecked.equals("intentionalEnd")) {
//            historyRadioButtonIntentionalEnd.setChecked(true);
//        }
//
//        if (currentlyChecked.equals("unintentionalEnd")) {
//            historyRadioButtonUnintentionalEnd.setChecked(true);
//        }

        return personalBestToReturn;
    }

    public void loadEnduranceHistoryRecordsFromDB() {
        //       -we only want to show 'ended with intentionalEnd/unintentionalEnd' after each entry if we have 'both' selected



        //if the cell that exists at the intersection of the pattern and modifier combo is null, then there are no
        //      records and tell the user that and that is it...
        if (getCellFromDB("HISTORYENDURANCE", currentEntryName(), "MODIFIERS",
                stringOfCurrentlySelectedModifiersInAlphabeticalOrder()) == null ||
                getCellFromDB("HISTORYENDURANCE", currentEntryName(), "MODIFIERS",
                        stringOfCurrentlySelectedModifiersInAlphabeticalOrder()).equals("")) {
            historyRecordsTextView.setText("No endurance records exist.");
        } else {//but if it isn't null, then we need to display them based on the filter created by the radiobuttons and checkbox

            String[] allRecordStrings = arrayOfRecordsBasedOnHistorySettings("HISTORYENDURANCE");


            int finalIndex = allRecordStrings.length - 1;

            //we will fill this up later
            String textForTextView = "";

            //these next 4 lines are used to keep track of which of our entries is the 'personal best' for unintentionalEnd and intentionalEnd
            currentIntentionalEndPersonalBest = -1;
            int currentIntentionalEndPersonalBestDateAndTimeIndex = -1;

            currentUnintentionalEndPersonalBest = -1;
            int currentUnintentionalEndPersonalBestDateAndTimeIndex = -1;


            //we are going to go through all the entries starting with the last one
            for (int i = finalIndex; i > -1; i--) {

                //      this format: (intentionalEnd/unintentionalEnd)(run length)(date/time)

                //since everything has been input in the same pattern, we can determine what is what by getting
                //  modulus of everything, and we will immedietly put everything right into our string 'textForTextView'

//                //this is a date/time
//                if (i % 4 == 3) {
//                    textForTextView = textForTextView + allRecordStrings[i];
//                }
                //this is a run length
                if (i % 4 == 2) {
                    int lengthInSeconds = Integer.parseInt(allRecordStrings[i].replaceAll("[^0-9]", ""));
                    //textForTextView = textForTextView + "Length: " + formattedTime(lengthInSeconds);


                }
                //this is intentionalEnd/unintentionalEnd
                if (i % 4 == 1) {
//                    if (historyRadioButtonBoth.isChecked()) {
//                        textForTextView = textForTextView + "Ended with a " + allRecordStrings[i] + ".";
//                    }

                    //these next two modifierals are used to check each record up against the current
                    //      highest entry to determine which is the personal best
                    if (allRecordStrings[i].equals("intentionalEnd")) {
                        ////Log.d("myTag", "intentionalEnd");
                        if (Integer.parseInt(allRecordStrings[i + 1].replaceAll("[^0-9]", "")) > currentIntentionalEndPersonalBest) {
                            currentIntentionalEndPersonalBest = Integer.parseInt(allRecordStrings[i + 1].replaceAll("[^0-9]", ""));
                            currentIntentionalEndPersonalBestDateAndTimeIndex = i + 2;

                        }
                        String personalBestMarker = "(PB)";
                        for (int j = 0; j < finalIndex; j++) {
                            int lengthInSeconds = Integer.parseInt(allRecordStrings[i + 1].replaceAll("[^0-9]", ""));

                            //this is a run length
                            if (j % 4 == 2 && j < i + 1 && allRecordStrings[j - 1].equals("intentionalEnd")) {

                                int challengerLengthInSeconds = Integer.parseInt(allRecordStrings[j].replaceAll("[^0-9]", ""));
                                ////Log.d("myTag", "challengerLengthInSeconds = "+Integer.toString(challengerLengthInSeconds));
                                ////Log.d("myTag", "lengthInSeconds = "+Integer.toString(lengthInSeconds));

                                if (challengerLengthInSeconds >= lengthInSeconds) {
                                    personalBestMarker = "";

                                }


                            }

                        }
                        textForTextView = textForTextView + personalBestMarker;
                    }
                    if (allRecordStrings[i].equals("unintentionalEnd")) {
                        ////Log.d("myTag", "unintentionalEnd");
                        if (Integer.parseInt(allRecordStrings[i + 1].replaceAll("[^0-9]", "")) > currentUnintentionalEndPersonalBest) {
                            currentUnintentionalEndPersonalBest = Integer.parseInt(allRecordStrings[i + 1].replaceAll("[^0-9]", ""));
                            currentUnintentionalEndPersonalBestDateAndTimeIndex = i + 2;

                        }
                        //String personalBestMarker = "(PB)";
                        for (int j = 0; j < finalIndex; j++) {
                            int lengthInSeconds = Integer.parseInt(allRecordStrings[i + 1].replaceAll("[^0-9]", ""));

                            //this is a run length
                            if (j % 4 == 2 && j < i + 1 && allRecordStrings[j - 1].equals("unintentionalEnd")) {

                                int challengerLengthInSeconds = Integer.parseInt(allRecordStrings[j].replaceAll("[^0-9]", ""));
                                ////Log.d("myTag", "challengerLengthInSeconds = "+Integer.toString(challengerLengthInSeconds));
                                ////Log.d("myTag", "lengthInSeconds = "+Integer.toString(lengthInSeconds));

                                if (challengerLengthInSeconds >= lengthInSeconds) {
                                    //personalBestMarker = "";

                                }


                            }

                        }
                        //textForTextView = textForTextView + personalBestMarker;

                    }
                }
                //this is the end explanation
                if (i % 4 == 0) {
                    //textForTextView = textForTextView + allRecordStrings[i] + "\n";
                }
                //textForTextView = textForTextView + "\n";
            }
            //and this stuff adds those personal bests to the textForTextView that will be shown in the history tab
            String personalBestText = "Personal Best:\n";
            if (currentIntentionalEndPersonalBestDateAndTimeIndex != -1) {
                personalBestText = personalBestText + allRecordStrings[currentIntentionalEndPersonalBestDateAndTimeIndex] + "\n" +
                        "Length: " + formattedTime(Integer.parseInt(allRecordStrings[currentIntentionalEndPersonalBestDateAndTimeIndex - 1]
                        .replaceAll("[^0-9]", ""))) + "\n";
//                if (historyRadioButtonBoth.isChecked()) {
//                    personalBestText = personalBestText + "Ended with a intentionalEnd.";
//                }
                personalBestText = personalBestText + "\n";


            }
            if (currentUnintentionalEndPersonalBestDateAndTimeIndex != -1) {
//                if (historyRadioButtonBoth.isChecked()) {
//                    personalBestText = personalBestText + "\n";
//                }
//                personalBestText = personalBestText + allRecordStrings[currentUnintentionalEndPersonalBestDateAndTimeIndex] + "\n" +
//                        "Length: " + formattedTime(Integer.parseInt(allRecordStrings[currentUnintentionalEndPersonalBestDateAndTimeIndex - 1]
//                        .replaceAll("[^0-9]", ""))) + "\n";
//                if (historyRadioButtonBoth.isChecked()) {
//                    personalBestText = personalBestText + "Ended with a unintentionalEnd.";
//                }
//                personalBestText = personalBestText + "\n";

            }
           // textForTextView = personalBestText + "______________________\n\nFull History:\n" + textForTextView;


//                    for (int i = 0; i < 3; i++) {
//                        Toast.makeText(MainActivity.this, allRecordStrings[i], Toast.LENGTH_LONG).show();

            //this is where we actualy show the text that we have built
            //historyRecordsTextView.setText(textForTextView);


        }

    }
    //--------------------------END HISTORY TAB ACTIVITY FUNCTIONS----------------------------------

    //--------------------------BEGIN ADD TAB ACTIVITY FUNCTIONS----------------------------------
    public void updateAddPatternNumberOfObjectsFromDB() {

        //we want to keep track of which item is currently selected so that after we refill it based on the max
        //      provided by the user in settings, we can set it back to what it was.
        int currentSelection = 0;
        Log.d("lots", "here2");
        if (!getCellFromDB("SETTINGS", "MISC", "ID", "6").replaceAll("[^0-9]", "").equals("")){

       currentSelection = Integer.parseInt(getCellFromDB("SETTINGS", "MISC", "ID", "6").replaceAll("[^0-9]", ""));
        }
        String fromCell = ""; //we make the string that will be populated based on what is in the cell dedicated to hold the
        //                              'max number of objects number'

        fromCell = getCellFromDB("SETTINGS", "MISC", "ID", "1");

        //this is just used for debugging
        //Toast.makeText(MainActivity.this, fromCell, Toast.LENGTH_LONG).show();

        //now we make a for loop that fills up the 'number of props' array by using the int value of 'fromCell' to determine
        //          which numbers to offer
        int maxNumber = 11;
        try{ maxNumber = Integer.parseInt(fromCell.replaceAll("[^0-9]", ""));}
        catch (Exception e) {
         /* This is a generic Exception handler which means it can handle
          * all the exceptions. This will execute if the exception is not
          * handled by previous catch blocks.
          */
            System.out.println("Exception occurred");
        }//turn fromCell into an integer

        addPatNumberOfObjectsForSpinner.clear(); //clear out the array so we can repopulate it

        for (int i = 1; i <= maxNumber; i++) { //loop through all the numbers going up to the user defined max

            addPatNumberOfObjectsForSpinner.add(Integer.toString(i));
        }

        //and apply the array we have just filled up to the spinner of object numbers over on the add tab
        ArrayAdapter<String> numOfObjsSpinnerAdapter = new ArrayAdapter<>(this, android.R.layout.select_dialog_item, addPatNumberOfObjectsForSpinner);
        numOfObjsSpinner.setAdapter(numOfObjsSpinnerAdapter);

//        String theString = "";
//
//        if(settingsMaxNumberOfObjectsEditText.getText().toString().equals("")){
//            theString = "nully";
//        }else{
//            theString = settingsMaxNumberOfObjectsEditText.getText().toString();
//        }


        //  Toast.makeText(MainActivity.this, Integer.toString(currentSelection), Toast.LENGTH_LONG).show();
        //    Toast.makeText(MainActivity.this, theString, Toast.LENGTH_LONG).show();

        //and finally, if we can, we set the selected number back to what it was before we came in here
        try {
            if (currentSelection <= Integer.parseInt(getCellFromDB("SETTINGS", "MISC", "ID", "1").replaceAll("[^0-9]", ""))) {
                numOfObjsSpinner.setSelection(currentSelection);
            }
        }
        catch (Exception e) {
         /* This is a generic Exception handler which means it can handle
          * all the exceptions. This will execute if the exception is not
          * handled by previous catch blocks.
          */
            System.out.println("Exception occurred");
        }

    }

    public void clearAddPatternInputs() {

        // Toast.makeText(MainActivity.this, "addJust got cleared", Toast.LENGTH_LONG).show();


        //set the entryName label that is right below the buttons
        addPatternEntryName.setText(currentEntryNameToShow());

        //this is the run entry name that gets displayed at the top, isn't in the add pattern section,
        //      but we want it to be set whenever addPatternEntryName is set
        runEntryName.setText(currentEntryNameToShow());

        addPatternDescriptionEditText.setText("");
        addPatternSiteswapEditText.setText("");
        addPatternLinkEditText.setText("");

//give the focus to the description editText so that a description can be easily added/read
        addPatternDescriptionEditText.requestFocus();
    }

    public void updateAddPatFromPatternsInputsSelected() {

        patternAddTabCurrentlyUpdatedWith = patternsATV.getText().toString();
//        String entryName = "("+patternsATV.getText().toString()+")" +
//                "("+numOfObjsSpinner.getSelectedItem().toString()+")" +
//                "("+objTypeSpinner.getSelectedItem().toString()+")";

        if (!patternsATV.getText().toString().matches("")) {


            //now we use the entry name to load the add on this pattern from the DB

            addPatternEntryName.setText(currentEntryNameToShow());

            //this is the run entry name that gets displayed at the top, isn't in the add pattern section,
            //      but we want it to be set whenever addPatternEntryName is set

            runEntryName.setText(currentEntryNameToShow());

            addPatternDescriptionEditText.setText(getCellFromDB("PATTERNS", "DESCRIPTION", "ENTRYNAME", currentEntryName()));

            addPatternLinkEditText.setText(getCellFromDB("PATTERNS", "LINK", "ENTRYNAME", currentEntryName()));

            addPatternSiteswapEditText.setText(getCellFromDB("PATTERNS", "SS", "ENTRYNAME", currentEntryName()));


        } else {//but if it blank,
            //set everything in addPattern to be blank

            addPatternEntryName.setText("No pattern name selected");
            //this is the run entry name that gets displayed at the top, isn't in the add pattern section,
            //      but we want it to be set whenever addPatternEntryName is set

            runEntryName.setText("No pattern name selected");

            addPatternDescriptionEditText.setText("");

            addPatternLinkEditText.setText("");

            addPatternSiteswapEditText.setText("");


        }

    }

    public void updateAddModifierFromModifierMATVselected() {

        //addModifierNamesForSpinner.clear();
        //we need to split the modifierMATV.getText().toString() into an array and fill the addModifiersSpinner with it
        addModifierNamesForSpinner = Arrays.asList(modifierMATV.getText().toString().split(", "));


        //make an adaptor with the array for the spinner and apply it
        ArrayAdapter<String> addModifierNamesSpinnerAdapter = new ArrayAdapter<>(this, android.R.layout.select_dialog_item, addModifierNamesForSpinner);
        addModifierNamesSpinner.setAdapter(addModifierNamesSpinnerAdapter);
        addModifierNamesSpinner.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showInfoDialog("Selected modifiers", "This list is filled with the selected modifiers in the 'Main Input' at the top of the " +
                        " screen. If this list is empty it is because no modifiers are selected above.");
                return true;
            }
        });


        //if there are no modifiers to be added, because our modifierMATV is nothing...
        if (modifierMATV.getText().toString().trim().length() < 1) {

            addModifierDescriptionEditText.setText("");//..clear the modifiers description editText

        } else { //but if it is not nothing, then we use what it is to fill the spinner

            //set the selection of the spinner as last string in the array(probably the most recently added)
            setSpinnerSelection(addModifierNamesSpinner, addModifierNamesForSpinner.get(addModifierNamesForSpinner.size() - 1));

        }

    }

    public void updateDBfromAddInputs() {
        //the only way we are going to update the DB is if there is actually a pattern name currently input
        if (!patternsATV.getText().toString().matches("")) {


            //now we go through and update the DB from all the different inputs
            myDb.updateData("PATTERNS", "DESCRIPTION", "ENTRYNAME", currentEntryName(),
                    addPatternDescriptionEditText.getText().toString());

            myDb.updateData("PATTERNS", "SS", "ENTRYNAME", currentEntryName(),
                    addPatternSiteswapEditText.getText().toString());

            myDb.updateData("PATTERNS", "LINK", "ENTRYNAME", currentEntryName(),
                    addPatternLinkEditText.getText().toString());

        }

        //debugging
        //Toast.makeText(MainActivity.this, getCellFromDB("PATTERNS", "DESCRIPTION", "ENTRYNAME", "@@##&&yy@@##&&2@@##&&Balls@@##&&") + " isInDB2", Toast.LENGTH_LONG).show();


        if (!modifierMATV.getText().toString().equals("") &&
                !addModifierNamesSpinner.getSelectedItem().toString().equals("")) {
            myDb.updateData("MODIFIERS", "DESCRIPTION", "NAME", addModifierNamesSpinner.getSelectedItem().toString(),
                    addModifierDescriptionEditText.getText().toString());

            //addModifierDescriptionEditText.setText("what");
            // Toast.makeText(MainActivity.this, " happenend", Toast.LENGTH_LONG).show();


        }
    }
    //--------------------------END ADD TAB ACTIVITY FUNCTIONS----------------------------------


    //--------------------------BEGIN GENERAL HELPER FUNCTIONS----------------------------------
    public void setSpinnerSelection(Spinner spinner, String spinnerItem) {
        int spinnerIndex = 0;

        for (int i = 0; i < spinner.getCount(); i++) {
            if (spinner.getItemAtPosition(i).toString().equalsIgnoreCase(spinnerItem)) {
                spinnerIndex = i;
                break;
            }
        }

        spinner.setSelection(spinnerIndex);
    }

    public String currentEntryName() {

        //we make the entry name by following this format @@##&&name@@##&&number@@##&&type@@##&&
        return buffer + patternsATV.getText().toString() +
                buffer + numOfObjsSpinner.getSelectedItem().toString() +
                buffer + objTypeSpinner.getSelectedItem().toString() + buffer;

    }

    public String currentEntryNameToShow() {

        return patternsATV.getText().toString() + " / " +
                numOfObjsSpinner.getSelectedItem().toString() + " / " +
                objTypeSpinner.getSelectedItem().toString();

    }

    public void hideKeyboard() {


        //this is used to get rid of our keyboard when we click off the pattern/Modifier textfield and
        //      onto something else, like a tab.
        if (getCurrentFocus() != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), 0);
        }

    }

    //given a string and a list, this checks to see if the list contains the string without caring about the case of any letters of either word
    public boolean containsCaseInsensitive(String s, List<String> l) {
        for (String string : l) {
            if (string.equalsIgnoreCase(s)) {
                return true;
            }
        }
        return false;
    }


    //--------------------------END GENERAL HELPER FUNCTIONS----------------------------------

    //--------------------BEGIN FUNCTIONS THAT INTERACT WITH THE DATABASEHELPER CLASS---------------------------------
    public String getCellFromDB(String table, String column, String row, String rowIdentifier) {
        String stringToReturn = "";
        //Log.d("getCellFromDB", column);
        Cursor res = (myDb.getCellFromDatabase(table, column, row, rowIdentifier)); //this is the request for that number, it is the 1st row so "1"

        //this -1 check is used to make sure that the column actually exists
        if(res.getColumnIndex(column)!=-1) {
            //i don't know why, but this gives us access to the number we just requested
            if (res.moveToFirst()) {

                stringToReturn = res.getString(res.getColumnIndex(column));
            }
        }

        return stringToReturn;
    }

    public List<String> getColumnFromDB(String table, String column) {

        List<String> columnToReturn = new ArrayList<>();

        Cursor res = (myDb.getColumnFromDatabase(table, column)); //this is the request for that number, it is the 1st row so "1"

        //i don't know why, but this gives us access to the number we just requested
        while (res.moveToNext()) {
            columnToReturn.add(res.getString(res.getColumnIndex(column)));
        }

        return columnToReturn;
    }

    public List<String> getAllFromDB(String table) {

        List<String> columnToReturn = new ArrayList<>();

        Cursor res = (myDb.getAllFromDatabase(table)); //this is the request for that number, it is the 1st row so "1"

        for (int i = 0; i < myDb.getFromDB(table).getColumnCount(); i++) {
            //i don't know why, but this gives us access to the number we just requested
            while (res.moveToNext()) {
                columnToReturn.add(res.getString(i));
            }
        }

        return columnToReturn;
    }

    public void addDataToDB(String table, String col, String textToAdd) {
        //this calls up 'insertData' from DatabaseHelper and inserts the user provided add
        //      the EditTexts from above which were taken from our Layout
        boolean isInserted = myDb.insertData(table, col, textToAdd);
        //boolean isInserted = myDb.insertData();
        //Toast.makeText(input.this,(myTextPattern.getText().toString()),Toast.LENGTH_LONG).show();

        //THESE TOASTS ARE JUST USED FOR DEBUGGING
//        if(isInserted)//if it successfully inserts our stuff, then it does this
//            Toast.makeText(MainActivity.this,"Data Inserted",Toast.LENGTH_LONG).show();
//        else//if not it does this
//            Toast.makeText(MainActivity.this,"Data not Inserted",Toast.LENGTH_LONG).show();


        //maybe this is not the best place for this, but since we are adding adta to the db,
        //  our stats might have just changed, so we should refresh them:

        //refreshStats();
    }

    public void updateDataInDB(String table, String col, String row, String rowIdentifier, String textSent) {
        //this calls up 'insertData' from DatabaseHelper and inserts the user provided add
        //      the EditTexts from above which were taken from our Layout
        boolean isInserted = myDb.updateData(table, col, row, rowIdentifier, textSent);
        //boolean isInserted = myDb.insertData();
        //Toast.makeText(input.this,(myTextPattern.getText().toString()),Toast.LENGTH_LONG).show();

        //THIS STUFF JUST USED FOR DEBUGGING, USING MORE SPECIFIC TOASTS IN ACTUAL PROGRAM
//        if(isInserted == true)//if it successfully inserts our stuff, then it does this
//            Toast.makeText(MainActivity.this,"Data Updated",Toast.LENGTH_LONG).show();
//        else//if not it does this
//            Toast.makeText(MainActivity.this,"Data not Updated",Toast.LENGTH_LONG).show();

        //maybe this is not the best place for this, but since we are updating adta to the db,
        //  our stats might have just changed, so we should refresh them:

        refreshStats();

    }

//    --------------------END FUNCTIONS THAT INTERACT WITH THE DATABASEHELPER CLASS---------------------------------

    //LOOK FOR ANYTHING THAT IS CURRENTLY CRASHING THE APP SINCE WE HAVE SWITCHED TO NEW PHONE:
    //      -bulk siteswap(maybe add pattern) dialog crashes when 'all prop types' is clicked

    //MISSION MAKE MANY SMALL CLASSES(roughly shoot for 100-500 line classes:
    //  -figure out exactly how to make a new class and make a small simple one
    //  -the basic start, stop, onBackPressed functions can all stay in the mainActivity class
    //  -Small classes to make:
    //      -the filter stuff can be their own class
    //      -some of the stuff between the --------------s can be made into their own classes,
    //          althought some of these may be too big and may be needed to be made into smaller classes
    //      -helper and format functions like the one that turns seconds into the hh:mm:ss format could be their own class


    //    bugs:
    //    -If i do an initial pattern, then delete it from history, it is still in the lists on the longhold pattern input
    //      -To make this not happen, i need to do a check after history is edited, i need to check to see if the history i just edited is now blank,
    //          if it is blank, then i need to check to see if the history is blank for the other run type as well(endut or drill). If
    //              they are both blank, then i need to remove the pattern from the list of patterns, and then i must update the list
    //                  that is to be populated in the longHold patterns
    //     -Make audio not play if it is at 0, that way record audio doesnt happen when it hasnt been set

    //notes from phone need organized:
    //    See if we can find any graphic audios that are both in eng and esp, after listening
    //      to a bit in eng, then in esp i think the cues of the voices and sounds may help
    //      me figure out what is going on.

    //THOUGHTS ON CHANGING TIMER METHOD:----------
    //  -it seems like whe the screen is off we can handle the timer differently than when it is on.
    //      when it is off we do not need to keep updating a variable, if we keep track of the system time that
    //      the run began, then maybe we can ust use that time when the app is opened again.
    //  -Timer seems to get off during long runs. To fix this we could just keep checking how
    //      long it has been by using system time.
    //  -I just left the timer run for like 3hrs and when i reopened the app it had to start
    //      fresh, run was lost. I think this means it is working really hard and stuff is building up and it crashes.
    //          I think we should make the run dialog not close from outside touch

    //THOUGHTS ON MAKING THE LOAD TIME SHORTER:------------------
    //  -we can put in a bunch of logs so that we can see what it is doing when.
    //  -remove as much as possible from the startup

    //SESSION/EDIT RUN ISSUE (sessions get messed up when editing runs length sometimes)
    //  -here is the bug: start a fresh history, do a run, end session, edit the time of the run by a factor of 1000
    //      do another run, end the session. the dates are messed up. for some reason it adds roughly the length
    //      of the edited original run to the current time to find the end time of the second session.
    //          -maybe we do not need to worry about this now, i do not know exactly what ideal would be,
    //              but as it is, it seems to fix itself, and probably will so long as the edits are not too crazy

    //NEXT UP TODO:--------------------------------------------------
    //  -hide the place that we can add a bunch of siteswaps so that people do not accidentally do it and
    //      have the app crash, but if they know about it then they can also be warned of the
    //      crashing situation. MAYBE make an obvious one with a few basic patterns as well.
    //  -AS it currently is, I think there may be problems if the bulk siteswap adder is done when
    //      there are already siteswaps existing, I am not sure, but I think we removed the check
    //      to make sure that there are no duplicates.
    //  -A way to put in notes before a run incase user wants to jot something down before a run
    //  -work on 'OPERATION NEW RANDOMIZER'
    //  -The inten and unintent records should be easily seen somewhere(maybe this should be togglable incase user doesnt want to see)
    //  -organize and consolidate all these lists
    //  -work on 'STATS'
    //  -work on 'OPERATION CATCH COUNT INPUT'
    //  -work on 'OPERATION FREESTYLE INPUT'
    //  -work on 'WELCOME DIALOG'
    //  -that space above and the space to the left of the '(hide filters)' textview on the hist tab
    //      ought to be clickable as well and should do what the '(show filters)' click does
    //  -work on 'ADD SITESWAP WIZARD'
    //  -work on 'ADD RUN/SESSION WIZARD'
    //  -A function to make a line graph by putting in variables so i can graph anything.
    //          -another type of graph that might be nice is pie graphs for seeing how time is allocated between runs that end intentionally
    //              or unintentionally, spent in drills or endurs, were PB or not
    //  -Make it clear that it means 'Modifiers with hiatory with currently selected pattern' and
    //      'Patterns with history with currently selected obj num and prop type'
    //  -Figure out how to annotate 1up 2up where every other throw with each hand is a backcross


    //OPERATION NEW RANDOMIZER:-----------------------------------------------
    //  -make clicking the square choose a random
    //  -randomizer settings:
    //      -prop type chk
    //      -obj num chk
    //      -pattern chk
    //      -min/max ss period length
    //      -with history ok
    //      -without history ok
    //  -POSSIBLE randomizer settings:
    //      -no PB in the last X days (define X)
     //  -longhold randomizer square should open a dialog with lists and settings
    //      -what currently happens when the square is clicked should happen when a button is clicked inside of the dialog that appears
    //                  when the square is clicked. it should say something like 'past modifiers with current pattern'
    //  -lists should be the modifiers lists currently in the square longhold AND pattern list currently in pattern longhold
    //  -randomizer settings:
    //      -figure out a good way to define specifics of the randomizer, any obj num, any pattern, any pattern w/ or w/out history,
    //              focus on patterns that have not had a pb beat in quite some time, any other way i can think of to limit the
    //              randomizer or define the randomizer
    //      -any pattern with certain restraints:
    //          -obj num, prop type, uninitialized, initialized, within a certain % of patterns that havnt been done in a while,
    //              havn't had a PB broken in awhile
    //      -if there is a button that does the random action, then a longhold on that button could open up a settings dialog
    //          for the randomizer. The randomizer can be very broad or specific, it can even pick obj numbers and types, and modififers,
    //          the modifiers could be either combinations that have been used in at least 1 run before or not
    //      -I really need a good randomizer for picking new pattersn for same obj num.i think a random button which
    //              has settings when you longhold it, maybe sliders for %s. Example: patterns that have no history0-100.
    //              Something like this, unsure of how to use the period lengths, i want a way to do this that gives lots
    //              of control over the distribution, but isnt too hectic.
    //      -There could be a way to limit the period length of the siteswap of the patterns that may be chosen by the randomizer
    //           -this needs to take into consideration that some patterns may not have a siteswap input
    //  -the random pattern on new object number thing can be removed once we have the new randomizer system installed



    //TO THINK ABOUT:-------------------------------------------------
    //  -after a run ends:
    //          -maybe final thought stuff should be optional,
    //          -maybe there should also be an optional 'notes' section on the run in general, not specifically the
    //              final thought/reason for intentionalEnding
    //  -some sort of an optional note to be attached to any run, not a final thought note, just a comment on the run might
    //      be nice.
    //          -we do have that for sessions now, maybe we don't also need to have one specifically for runs, dont want too much clutter

    //GENERAL OBSERVATIONS/TODOS:--------------------------------------------------------
    //  -the code, in general needs to be cleaned up, commented, made easier to navigate
    //  -the app runs slow, keep track of exactly where it is going slow, see if anything can be done to make it better
    //  -figure out how to use github and upload code to it once we are at a good release point

    //BUGS:-------------------------

    //NEEDS TESTED:--------------------
    //  -all the history checkboxes, make sure that with none checked that all items get shown, and that checking an box removes
    //      that type of item from the history list

    //UNSORTED EVENTUAL IDEAS
    //  -Selecting an obj num already selected should pick random pattern too
    //    Eventual ideas:
    //  -Voice recognition and questions from app as well as telling user what patterns to do (is this possible?
    //                  Have app read user defined pattern names?). Then it can just be a back and forth discussion,
    //                  no need to take the phone out. Everything can still.be on the phone, but think of how nice
    //                  it would be to just be able to talk back and forth with a coach!

    //STATS:------------------------------------------------------------------------
    //  -LIST OF STATS I KNOW I WANT TO ADD:
    //      -DRILL SECTION:
    //          -percent of drill runs(where 'restart set on unintentionalEnds' box is checked) that are finished without unintentionalEnds
    //          -in drill runs(where 'restart set on unintentionalEnds' box is checked), percent more attempts than sets in drill runs
    //      -NON-NUMERIC:
    //          -longest endurance run that ended in a intentionalEnd/unintentionalEnd
    //              -these could just show the length of the runs, but then a longClick on the time could open a dialog that says
    //                  the pattern/modifiers
    //          -info on the final thoughts for unintentionalEnds sounds good, maybe just a list of most common thoughts before
    //                  unintentionalEnds/reasons for intentionalEndes
    //                  -a list of all reasons for unintentionalEnds/intentionalEndes from most common to least with %s next to them
    //                      -another version of this list only taking into account the runs that resulted in breaking personal bests
    //  -start making stats help bubble, see the 'HELP BUBBLES' section below
    //TO DECIDE ON:
    //
    //POSSIBLE STATS TO ADD:
    //     -maybe additional specific info for longClicks on stats numbers
    //   -unique combinations of modifiers:
    //          -it would have under it the same stuff as 'combinations of modifiers/patterns', but it would just be for modifiers
    //          -entries...
    //                -the same as 'unique combinations of modifiers...' but just for entries
    //                      -the issue with both of these is that it gets a little confusing when we already have combos
    //                          of modifiers/patterns
    //                      -a possible fix for this issue is detailed help bubbles, and even a list that appears on long
    //                          click on some numbers, like with this one we could actually show all the modifier combos
    //  NEW STATS LAYOUT:
    //   -probably not going to d this for now, maybe tuck it away as a possible eventual idea in the main to do list
    //   -main collapsable headers, inside each section we still have the smaller sections like 'Total number of...', they are just shorter
    //      because they only have to deal with their smaller group. Also, for something like 'total number of runs', that could be in
    //      general stats as it's own line since it is now going to be alone
    //      -GENERAL: this would contain all the stuff that we have now that applies t the app in general or runs in general
    //      -STREAKS: maybe we want this to be a section, I am not sure, maybe it should just go in with endurance/drill stuff
    //      -ENDURANCE: all the stuff that only applies to endur
    //      -DRILLS: all the stuff that only applies to drill
    //SESSION STATS:----------------------------------------**summary:these are all stats relating to the sessions directly,
    //                                                          these could be shown with the other stats on the stats tab and/or
    //                                                          shown alongside the stats in the session summary when a session is ended
    //                                                          we could just show the average of each stat in the summary in parenthesis
    //                                                              next to the stat for that session**
    //      Averages:
    //       session length
    //           Amount of time spent juggling in runs, vs inbetween time
    //              -this one seems a bit pointless to me, maybe go without it
    //          Amount of time spent on endur,drill,intentionalEnd,unintentionalEnd,restart,dont
    //          Number of runs, endur...
    //      Extremes:
    //          Longest session ever
    //          Most pb brakes, initial, drill, endur... in a ses
    //      Totals:
    //          number of sessions (add to existing section)
    //          number of sessions with at least X personal breaks
    //          most days doing at least X sessions every Y days (add to existing section)
    //      Other:
    //          Something dealing with most common point in sessions to break a personal best,
    //          this could be time or run number or something else, not sure yet, it is kinda vague
    //           Something dealing with most common lengths of session that result in lots of PB breaks

    //OPERATION CATCH COUNT INPUT:--------------------------------
    //-There should just be an input for number of catches after a run is completed
    //-We can still determine the PBs based on time
    //-for patterns where only a few catches are gotten, instead of messing with the app after every attempt, could
    //      keep track of run with highest number of catches, then input that manually by just letting the run go once
    //      without juggling, stopping it at an appropriate time, and then inputting the number of catches

    //OPERATION FREESTYLE INPUT:--------------------------------------------------------
    //  -this is a timer that keeps track of how much time is spent messing around, what props/numbers were involved, it doesn't
    //      keep track of drops or anything like that
    //  -one option is just to leave this as pattern in one of the numbers(maybe something high, just call it freestyle, do endur
    //      with it and just don't tell it if i drop, leave that in the notes of the pattern
    //  Next Up:
    //      -make a note in the general app notes explaining this method of keeping track of freestyle sessions

    //WELCOME DIALOG:-------------------------------------------------
    //    A welcome dialog that pops up behind the intro dialog. It can be toggled from automatically showing.
    //              It will contain any presets set by the user for things like modifiers, obj num/type, pattern. It can also have
    //              'missions' to be done.
    //
    //    These missions could range anywhere from randomly selected to the ai stuff.
    //
    //    A mode where user inputs a trick/pattern that they want to do for a certain amount of time, then the
    //              app gives daily or regular missions, and some way to keep track of mission progress

    //ADD SITESWAP WIZARD:---------------------------------------------------
    //    Make a way to add a bunch of siteswaps (all the lists i have), maybe make a little wizard for adding
    //                  the kinds of siteswaps that you want. Period length, obj number..

    //ADD RUN/SESSION WIZARD WIZARD:---------------------------------------------------
    //
    //    A wizard/form to input past runs/sessions, either by selecting a session, or creating one with begin&end info
    //


    //THE DROP/CATCH INTENTIONAL/UNINTENTIONAL SITUATION:
    //  -I think that the distinction between whether I ended intentionally or not is more important than whether I ended
    //      with a intentionalEnd or a unintentionalEnd, for instance, if there is a collision that I cannot recover from and I manage to gather everything,
    //      that should be seen as very similar to if I unintentionalEndped, not very similar to if I just decide to end at a certain point and
    //      intentionalEnd everything.
    //  -instead of just intentional and unintentional, should there be something for accidentally left the pattern, but didn't lose control?
    //      maybe a forced catch vs decided catch v drop. i dunno..


    //HISTORY EVENTUAL TODO:--------------------------------------------------------
    //  -History should be orderable, instead of just chronological. Sort by run length might be nice.
    //  -make a way to sort history by final thoughts, maybe will need to use some sort of a spinner
    //          to select the possible final thoughts
    //  -make a way to check and see how much time has been spent on specific patterns, this can be without modifiers
    //          or with modifiers
    //  -make a way to view history with a line graph

    //RUN EVENTUALLY TODO:--------------------------------------------------------
    //  -The drill stuff for the headphone button click
    //      -right now the visibility of the headset button is hidden when the drill radio button is clicked,
    //              go here to fix it: fixDrillVisibility
    //      -find 'operation headphone button', there are several different areas marked with 'todo'
    //              -organize the 'drill headset button' notes so that we know exactly what we want to do
    //  -make a way to input number of drops

    //INTRO THOUGHTS:------------------------
    //   -there should be lots of emphasis on getting the new user introduced to the app and it being as easy to learn as possible
    //      -I think the quicker we can get the user actually juggling, the better, maybe the intro screen should be brief with an option for
    //          a more detailed intro
    //   -In the second paragraph of the current intro, explain that the user should also select endur(or drill), we want to tell the
    //      brand new user exactly what must be done to begin juggling
    //  -Maybe instead of on the settings tab, we want to put this with the intro somehow? (Maybe some sort of a setup wizard,
    //      if so then the wizard should be able to be run any time.):
    //               -make a button in the settings tab that adds siteswaps to the app, maybe several different amounts, allow user to just
    //                   add certain types of siteswaps, multiplex, sync, beyond a certain period size
    //                -when you click the button, a dialog opens up with a bunch of checkboxes, the user checks he ones they want, and
    //                     then clicks add. If they check them all, then they get a ton of siteswaps.
    //               -so far as initial siteswaps go, maybe the app should start with about 10 per object or so, the most basic siteswaps, and then there
    //                    should be a way to add more

    //Operation Session EVENTUAL IDEAS:--------------------------
    //  -Ai coach creates a session based on a certain amount of time and guesses as to how long endurances/drills will take
    //  -By showing ai assistant,coach,university (where new coaches can be selected and essentiallyy created),
    //      maybe we can keep ppl motivated to use the app more. I think some sort of partially showing them what is eventually available is good, maybe use a dialog to hide stuff, but some is revealed

    //EVENTUAL:-------------------------
    //  -work on the ai tab

    //BULK SITESWAP:-------------------------------------------

    //BUGS(PROBABLY FIXED):
    //  -need to limit the amount of the 'Endurance attempt canceled.' messages, maybe only put them in the onCancelListener

    //BUGS:

    //MISC THOUGHTS:
    //    A way to change mods after the fact maybe, so that a pattern can be started simply, but then if it
    //              results as very good, then more info could be added? I dont know how good an idea this is though.
    //
    //

    //MAIN INPUTS:---------------------------------------------


    //HISTORY:----------------------------------------------

    //SETTINGS:---------------------------------------------------------
    //  -organize the settings tab
    //      -postpone this until we are more sure that we have all the settings we want,
    //          for instance we may want some sort of a button to copy all patterns from one prop to another

    //PATTERNS:----------------------------------------------

    //MODIFIERS:------------------------------------------------

    //THOUGHTS FROM PHONE THAT NEED ORGANIZED:-----------------------------

    //ADD TAB:------------------------------------------

    //BULK SS:-------------------------------------

    //STUFF TO LOOK FOR/THINK ABOUT DURING TESTING:----------------------------------------------
    //  -better ways to explain stuff with the help bubbles.
    //  -what should the stats tab include

    //MAYBE TO DO:-----------------------------------------------------
    //  -when a modifier is deleted, check to see if the user want to just delete that modifier or all modifiers that only differ due to
    //      special throw sequence.
    //  -a button in the 'ADD' tab that is 'copy this pattern to another prop'

    //HEADPHONE BUTTON:
    //  -in drill(restart set on unintentionalEnds),we could use the headphone button to indicate that a unintentionalEnd has happened, or if a set has ended and
    //      the audio is sounding, then we could use the headphone button to start the next set(this is for both kinds of drills)

    //CODE CLEANING GRUNT WORK
    //  -I think all the onLongClickListeners in stats could be done by putting all textViews that need a helpBubble into an array and then cycle through them
    //      giving them on click listeners. We could use a case setup to give the proper help bubbles based on the text

//^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

    //IMPORT/EXPORT STUFF:

    //THOUGHTS ON STARTING STATE OF THE APP:---------------------------------
    //  -the amount of patterns/modifiers could be selectable in settings so that the user is not overwhelmed with a bunch of siteswaps
    //      that they do not actually need.

    //AI assistant:-------------------------------------------------------
    //  -it is not quite a coach, but it could help pick modifier/pattern combos that have not been done in awhile and thus
    //      are likely to be able to have a new personal best set.
    //  -it could randomly pick a modifier/pattern combo
    //      -in order to avoid picking multiple special throw sequence modifiers that are the same, we should probably use the
    //          '-' symbol to prevent it from doing so
    //      -maybe this means that stuff like 'listen' should also use a '-' so that we could use things like listen-silence, listen-music...
    //  -i don't know if we would need it or not if we just use the '-' system, but maybe we would want a way to say never use certain
    //      modifiers to be used together.

    //MINOR ANNOYING BUGS:---------------------------------------------------------

    //SUPER MINOR ISSUES THAT MIGHT NOT NEED FIXED:-----------------------------------
    //  -I want to be able to tap on the blank area below the tabs to make the keyboard go away.
    //          as of now I can make it go away by clicking a tab or clicking the back arrow.
    //  -long siteswaps can not be seen in the patternsMATV unintentionalEnddown because they are too long
    //              -they can be seen once they have been clicked on in the add tab, (eventually in the juggle tab), and by scrolling
    //                      in the main editText once they have been selected
    //  -the addModifiersSpinner starts out being unable to be clicked on(nothing happens)
    //          but once it has had something in it, even when it is empty it still pops up like it has a string of nothing in it
    //                  it would be nice if it were to just go back to how it was when the app loaded before there was ever anything in it
    //          -Maybe look into what exactly changes one it has had something and see if it is possible to revert to
    //                  how it was when it began
    //  -consider switching icon to the picture of a cartoon ant in an anthill that is available for reuse from pixabay

    //PRETTY MINOR THINGS THAT COULD BE ADDED:-------------------------------------------
    //  -if ever a table is missing, we could make a fresh one automatically. this might not be a big deal because
    //      the only way it could happen is if the user edits their db and deletes a table foolishly


    //MISC THOUGHTS:--------------------------------------------------------
    //  -It could tell you how many records you have broke in the last x hours or days.


    //RUNS:---------------------------------------------------------------

    //EVENTUAL NETWORK ASPECT:
    //  -Some way to show users the global stats to compare theirs to would be nice.
    //      I dont know if it would be better to give them usernames/leaderboards or just general stats


    //AI TO DO NEXT:-----------------------------------------------------
    //  -go through all the AI stuff below and organize it as much as possible
    //  -make a simple AI that tells user what to do
    //      -what exactly should be done for this?
    //          -a 'use ai' button in the juggle tab which fills the main inputs and starts a juggling run
    //          -maybe some place to input what it is that the user wants the AI to help them with
    //              -restrict certain props or object numbers or patterns
    //  -figure out how to compare different AIs to each other via users performance in order to see which AIs yield the best results
    //          -this will probably involve comparing base(initial) records to records over time
    //          -maybe the fact that new jugglers improve much more rapidly should be taken into account
    //BEGINNINGS OF A PLAN FOR AI:
    //  -There should be different AI personalities that can be plugged into formic
    //  -Personalities can be defined by filling out a questionair w/ percentages & yes/no & whatever else. For example:
    //          -percentage of time AI shows user personal records before they begin an attempt
    //          -percentage of time AI asks for stop(finish) time after an attempt
    //          -percentage of time AI tells user what to do(as opposed to AI telling user to decide
    //          -percentage of time AI lies to user about what their personal best is
    //          -percentage of time AI presents new patterns vs reviewing patterns that have been done before
    //                  -imaginably we also want a way to go check exactly what records are, but this may be discouraged
    //                          so that the AI can most effectively use its lies to try to encourage progress
    //                  -percentage of time AI exagerates times upward as opposed to downward
    //  -Personalities could be stored in .db files
    //          -they could be user definable
    //          -importable/exportable for sharing AIs with other users
    //  -I think the user should be able to reject AIs suggestions, however there should maybe be a 'cooperation rating'
    //  -the .db files for personalities could all start with AI, and then inside of Formic the AI to use could be selected
    //          -maybe... we might not want to encourage the use of multiple AIs so that w can more clearly compare results
    //              of specific AIs
    //  -There may be some part of the AI coach that should balance learning new stuff with reviewing stuff that has been done.
    //EVENTUAL 'AI COACH' STUFF
    //       -(patterns/Modifiers could have some sort of priority ratings so that certain things could
    //               be focused on, and other patterns/Modifiers could be put on hold for now, but not deleted
    //               from the DB list
    //       -maybe some sort of 'trick difficulty' could be determined by the length of the drills/records for that trick
    //                  not sure if this would be useful or not
    //  -there could be a 'ai decides pattern/Modifiers' button
    //          -even if the ai isn't too smart, just having a random pattern chosen might be nice
    //          -it could have a few response buttons as well, such as:
    //                  -never recommend this exact pattern/Modifiers combo again
    //                  -never recommend this Modifiers combo again
    //                  -give me a new recommendation for now(this option may be redundant to just clicking
    //                          the original 'ai recommend' button
    //                  -Formic could tell the user to stop to take the responsibilty
    //                               away from the user. one less thing to worry about.
    //  -the ai could sometimes tell the user what their personal best is for a pattern, and sometimes not, and sometimes
    //              it could lie about what their personal best is. The user could know that it sometimes lies.
    //SIMILARITY STUFF:
    //  -maybe want a similarity rating between props so that similar tricks with different props can be used as similar patterns,
    //          for instance we would want balls to be seen as more similar to beanbags than clubs are to rings...
    //  -the similarity ratings and similar tricks will be useful in choosing what to recommend to user i think
    //  -similarity stuff is determined automatically based of the attributes as well as
    //                          other things possibly(such as object#)-----------
    //  -similar tricks are other tricks that have some of these attributes
    //  -similarity ratings indicate just how similar 2 tricks are
    //  -use attributes to determine how similar things are
    //              -siteswap attributes should just happen in the background because if we actually make attributes for
    //                      them then they are going to completely flood the attribute list
    //              -we might even want to use prop type in similarity rating so that things like
    //                      mills mess balls and clubs are very similar
    //              -this means having the same name as anther entry(just with different props) should increase similarity rating
    //      -similarity ratings could be partially set by manually by user
    //      -similarity ratings could be partially set by looking for words in descriptions that match
    //TIMER(AND RELATED) STUFF:
    //  -maybe make random whether or not the finish times will even be recorded.
    //  -maybe a toggle to hide timer
    //  another drill timer which allows for just juggling, with unintentionalEnds counted, or uncounted, where user just
    //      keeps working on the pattern as long as they feel like. So, it would give them a count-up timer.
    //          -this idea came while doing breathing exercises and seeing a parrallel there, breathing/meditation
    //                      doesn't need to be done for X seconds and there isn't a 'mess-up' like unintentionalEndping in juggling
    //                      so the parallel, essentially, for that in juggling would just be, juggle, as long as you want,
    //                      and if you want to see how long you want to juggle over time, then use tihs timer.
    //          -it has made me wonder if maybe a 'custom timer' creator would be a good idea, it might be a little
    //                      complex/daunting, but if I gather up every different possible aspect that I can come up with
    //                      and allow them all to be combined as the user wishes, then new timers could possibly be created
    //          -a way to deal with the fact that a custom timer creator may be kind of overwhelming could be
    //                          to make 2 different apps, or maybe just a setting that makes the app a more
    //                          advanced app.
    //          -There might be something to getting the user in the habbit of hitting start/stop and not seeing/knowing
    //                  results. A level back from even this is having the app give commands to the user, and so long as the
    //                  user confirms, then the user never needs to make decisions. maybe different settings, sometimes user
    //                  picks what they do, sometimes they are told. I think getting away from knowing immedietaly what
    //                  records have been beat and such and instead seeing more longer term results may result in being
    //                  able to associate more long-term behavior with those results.
    //          -if app only sometimes cares to hear about results of endurance attempts then it may make the user go more
    //                  into a headspace of 'not being recorded' even when they are since they never know if they are or not.
    //                  this could be set with a defined % by user or a defined % by ai.
    //          -ai could keep track of whether more gains are made while ai is choosing what to do or while user
    //                  is choosing what to do. if it is often user, then ai could look for things user is doing and
    //                  behave more like that.
    //          -so far as ai being in control, possibly we would first need the user to put in some general goals for the ai
    //          -voice recognition/not showing the user the time when the timer is clicked 'stop'
    //  -Eventual 'keyword' thoughts:
    //      -make the ability for the user to apply keywords to modifiers and patterns, these can be used to find other similar patterns
    //          and modifiers.
    //      -we can use MATVs to choose them, but new words can also be added which will then be used to fill the MATVs for the other
    //          patterns and modifiers.
    //      -keywords are anything that describes the pattern/modifier
    //          -Examples: body throw, difficult, exhausting, goofy...
    //  -AI COACH THOUGHTS:-------------------------------------------------------
    //  -There could be something set up so that the user must do a certain number of runs before they unlock AI assistant
    //      or AI Coach
    //  -There could be an ai coach tab where you create ai coaches. Each table holding the historys should hold the ai
    //      brains as well so that when historys get uploaded and shared, the coach responsible can also be shared.
    //  -There should be a coach that is just 'Pick a random similar pattern', and one that is the same but increasing
    //      or decreasing in difficulty (so we need a difficulty rating).
    //  -Coaches should have a list of patterns they have access to.
    //  -A version without ai coach should be packaged together and offered around online. Maybe it should have an 'Ai
    //      coach comming soon' message on another tab.
    //  -Only 1 coach at a time or multiple?
    //      -i think only 1 because we want to be able to more clearly see the affect of a coach over time.
    //IDEAS ON HOW TO INTRODUCE AI:------------------------------------------------
    //  -after the user has used the basic app for a certain amount of time/usage, they unlock access to the ai assistant
    //  -after a certain amount of time/usage with that, they unlock access to a pre-defined AI coach
    //      -by using a pre-defined, they can try one that I think is decent, and I can get results from the AI coaches performance.
    //         Whenever I want to, I can switch this AI coach out with other AI coaches to test different things out.
    //  -after a certain amount of time with a pre-defined AI coach, the user becomes able to:(here are some different possible ideas)
    //      -choose from a selection of AI coaches
    //      -define a simple AI coach with slide bars
    //      -define a complex AI coach with slide bars
    //  -the results of different AI coaches should be able to be viewed by anyone
    //  -AI coaches made by users could be put up with their track record, and able to be selected by other users
    //      -maybe a sort of currency could even be created so that by using coaches for a certain amount of time, you can the ability
    //          to create/use other coaches


    //AESTETICS THOUGHTS:---------------------------------------------------------
    //  -more roundedness and less sharp corners
    //  -model after the 'apnea' app
    //
    //COLOR SCHEME THOUGHTS:
    //  -less colors may be better,
    //          maybe just use on color for 'selected tab' and one color for 'not selected tab'
    //  -use colors sparingy, so that they can be used to draw attention to places we want attention
    //          mainly sticking to a black/white/gry color theme with other colors sprinkled in

    //EVENTUAL SITESWAP ANIMATOR STUFF:--------------------------------------
    //  -there may be a way to download a gif created by siteswapbot for use with offline viewing of a siteswap, this would not be needed
    //      if we just get a siteswap generator to work with the app. BUt maybe it would be able to just have the location of the
    //      gif in as the url link in the link section. might be worth a check-a-roo.
    //          -another possibility is to try and download the website, but i don't know if this really is a possibility or not.
    //  -I think the ideal option is to create our own siteswap animator and either have it built in to the app or have it be a
    //      partner app of some sort that can easily be called up and given a siteswap to animate at the same time. A built-in feature
    //      would probably be best though.

    //CODE ORGANIZATION:---------------------------------------------------------
    //  -i think the stuff in addModifierNewBut(), updateAddModifierFromModifierMATVselected(), updateModifierMATV()
    //          all need to be reorganized, I think some stuff is getting done twice, or at times that i shouldn't.
    //          for example, there is no need to have fillAllTabsFromModifierATV include stuff that isn't actually what the name implies
    //  -i think we are sometimes using gloabal variables where local variables would suffice
    //  -descriptions of each function might be nice

    //SETTINGS EVENTUAL POSSIBLE IDEAS:-----------------------------------------------
    //   -phone announcement recommendations to remind you to do certain patterns
    
    //HISTORY EVENTUAL POSSIBLE IDEAS:
    //here are some notes for the 'exactModifiersOnly' idea, probably not really needed, and may just clutter things,
    //      but basically it is a way to view all history records that include the modifiers chosen by the user
    //      in the main modifierMATV-
    //if we do want exactModifiersOnly, then we go ahead and fill our list with the one relevant cell based on
    //      the current entry name and modifiers combo
    //but if we don't want exactModifiersOnly, then we need to go ahead and fill our list with all the modifier
    //      combos that include the indicated modifiers
    //              -we should split the readout up based on the different groups
    //                  of modifiers.
    //              -in order to get all history entries that include the selected modifiers, we must:
    //                  make a super list of 'allRecordStrings' from every modifier that includes the provided modifiers
    //                  this means checking to see if each modifier contains each of the provided modifiers individually,
    //                  not as an alphabetized list. once we have our super list, it should be business as usual
    //              -want to get the 'personal best' of them compared against,
    //                  if the user wants a 'personal best' for a specific combonation of modifiers, then they
    //                  can use the 'exact modifiers' setting
    //  -there should be different ways to view history, not just the history of a particular pattern/Modifier combo,
    //              but also things like looking back on history of broken records
    //              -maybe visual representations/graphs could be nice to see progress over time
    //  -something that lists tricks that have not had records set for them in a long time would be nice,
    //              especially if this list could use some sort of algoritm to determine which tricks amongst those
    //              are most likely to be able to have new records set with
    //  -the ability to manual alter/create/delete history items
    //LINE GRAPH:-----------------------------------------------
    //  -A graph that shows the progress over time of a certain pattern/modifiers combo would be nice
    //      -there are a few ways of doing this, it could be 1 chart for each combo, or it could be a chart per pattern with different
    //          lines indicating different modifiers
    //      -drills could be a separate chart, or they could be dots on the same chart as endurance runs
    //      -each run could be a clickable dot on the graph
    //  -there could be one graph that shows everything, and even multiple patterns could be shown on it at the same time so that
    //      the progress of different patterns could be compared up against each other.

    //RUNS EVENTUAL POSSIBLE IDEAS:------------------------------------------------------
    //  -an alternative to using a timer could be using the bluetooth balls
    //           -maybe there could be an optional beep or sequence of beeps to indicate that timer is starting
    //  -some record could be kept involving back-to-back-toback.. endurance attempts(and maybe drill as well)
    //      -this goes further, maybe just the ai, or maybe some way to view it in the db, but time/days since attempts might be helpful
    //                  even with crossover, like how much time between drills and endurance attempts
    //  -possibly make another sound for the record throughout all possible modifiers. Whatever the best record
    //                  for that pattern is, maybe let the user know when that is broken.

    //SETTINGS EVENTUAL POSSIBLE IDEAS:
    //  -could make cells that hold the stuff in patternsATV and modifierMATV and other places like that to save what
    //              is currently in their editTexts when the app is closed so it is all there when app is reopened

    //ADD EVENTUAL POSSIBLE IDEAS:----------------------------------------------------
    //  -for 'bulk siteswaps' maybe just use a spinner instead of the multiautocomplete for the object type,
    //          it looks nicer and then we wouldn't need a label, and it isn't too much to just add a different list for each
    //              object type. I don't know, it isn't really very important.
    //  -IN ADD, maybe there could be some way to input something like 'under-the-leg' and have it automatically add a bunch of
    //          patterns like: 3bLh(under leg), 3b<every other>Lh(underLeg), 3b<every third>Lh(underLeg)...
    //                    -THERE ARE SO MANY PATTERNS THAT COULD AUTOMATICALLY BE MADE JUST OFF OF ANY BODY THROW
    //                          ***THERE MIGHT BE SOMETHING TO THIS, LIKE A PLACE WHERE ALL THE TRICKS THAT WILL BE AUTOMATICALLY
    //                                  MADE IF THE USER INPUTS A A BODY THROW***
    //                      ***MAYBE SOME SORT OF AN INPUT WIZARD*** COULD A WIZZARD LIKE THIS BE USED FOR SSs/OTHER TYPES OF TRICKS?
    //  -a way to search pattern/modifier descriptions would be nice for finding names of patters that the user can describe,
    //          but maybe don't know the actual name of the pattern
    //  -having the unofficial/official records list moved into app could be cool to compare yourself aainst the world

    //SITESWAP ANIMATOR SISTER APP:-------------------------------------------------
    //  -it would be nice if there was another app that could get called up to animate a siteswap. This would be better than the current
    //      'link' setup because it would also work offline as well as it could have speed set and other siteswap animator things.
    //  -in order to do this we need to be able to open an app and pass it information, like opening a browser with a link.

    //EVENTUAL POSSIBLE OPERATION 'LOAD UP WITH PATTERNS'---------------------------------------------------------
    //  -figure out if there is a way to quickly insert a bunch of siteswaps into the db
    //          -we may actually want to fill up a db with 3rd party software and then use that pre-made db in the software
    //                  instead of using 'addFirstTimeRunDatabaseData()' and instead of creating the db in databasehelper
    //                  -or maybe create a db in formic, export the .db and then use the filled up .db as the starting .db
    //          -a well put together reddit post could be created asking for help gathering information/tricks
    //-maybe eventually make a button for adding 'body throw' patterns in bulk
    //      -this is described below in 'ADD EVENTUAL POSSIBLE IDEAS'
    //ADDITIONAL STUFF THAT COULD BE DONE TO 'bulk siteswap':
    //      -do a stronger validity check, instead of just the average, do the countdown formula,
    //          there is an explanation here: https://books.google.com/books?id=qL7CCgAAQBAJ&pg=PA66&lpg=PA66&dq=formula+determine+if+siteswap+is+valid&source=bl&ots=OP7sQ3RHOD&sig=rgmqAzc4F7sCD59SvjykII7jE8E&hl=en&sa=X&ved=0ahUKEwifyZq2qIDRAhVF6oMKHWWwC70Q6AEINTAE#v=onepage&q=formula%20determine%20if%20siteswap%20is%20valid&f=false
    //              -the main issues with this is taking into account sync and multiplex notation
    //      -make sure there is not more than 2 digits in any set of '( )'s
    //              -with an exception for multiplex throws
    
    //VOICE RECOGNITION:--------------------------------------------------------------
    //  -this goes hand-in-hand with the 'keep screen from timing out' setting since we need to stay awake to hear user speak
    //  -incorporating voice recognition should not be too tough, we already have it set up in the hue app,
    //          just a matter of copying code over and modifying it
    //  -there could be voice recognition for simple commands such as intentionalEnd or unintentionalEnd or start over
    //          -if this is the case then there needs to be some sort of screen timeout prevention or we need to be able to take voice
    //                  commands from a timed out screen. I am pretty sure that if listneing for a voice command the screen must be on
    //  -THE PLAN:
    //      -instead of clicking 'intentionalEnd'/'unintentionalEnd'/'ok' in any of the runs I should be able to just say it if I have voice on
    //              -this is 'intentionalEnd'/'unintentionalEnd' in endurance
    //              -'unintentionalEnd' in drill if 'restart set on unintentionalEnds' is checked
    //              -'ok' after a drill set is complete and it is waiting for the user to start the next set
    //      -voice should be turned on in settings
    //      -it should be a new popup that goes below or partially over top of the timer, but not hiding the timer
    //  -NEXT UP TO DO:
    //      -get the code from the hue app and move over the relevant bits

    //QUESTIONS?---------------------------------------------------------
    // -should similarity rating be %100 based off of trick attributes or should it be overwrtitable/effectable by the
    //                  user?
    // -should expandable views be added anywhere to keep things less cluttered? (such as in the 'add'tab activity)

    //OTHER PROJECTS:------------------------------------------------------
    //  -what other projects do we want to do?
    //          -make a list of prjects by using the posterboards/notebook/unfinished projects
    //                  -an app for tracking weight training progress may be good
    //  -you could always begin working on colorChange and/or juggglow stuff
    //  -don't forget that formint has been started

    //NOTES ON WHAT I DID WITH THE '( )' ISSUE (CAN BE DELETED IF LONG TIME WITH NO ISSUES:
    //              -we need to be careful about what happens when the user adds '(' and ')' in because it could
    //                      break things just because they need escaped
    //              -find everywhere that uses:
    //                  -entryname/currentEntryName
    //                           )( becomes @@##&& line2717 2661 1181
    //                  -split("\\)\\("
    //                          line 2816 uses )( i believe it will work in conjunction w/ the above entryname stuff
    //                  -split("\\)"
    //                          line 2325
    //                  -the substring(1) stuff can go away because we will not be using the begining ( anymore, just the @@##&&
    //                          between each of the name/numOfObjects/objType
    //                  -probably should replace all the parenthesis with some random string of chars like '@#&#@' or
    //                        something so it is never accidentally added and messes things up. @#&#@ doesn't need to be escaped at all
    //                                      -there could even be a check for our special string of chars
    //                                              that keeps it from ever being added into a textEdit



    //NOTES ON BULK SITESWAP:
    //      -I only do the averaging test to determine validity of siteswaps and get the number of objects if the siteswap
    //          is indeed valid.
    //      -all advanced siteswap notation are allowed, (), [], and 'x' and '*'
    //              -sync notation: indicated by ( ),
    //                              only uses even numbers,
    //                              x indicates that the throw is a cross throw
    //                              an '*' at the end indicates that the entire sequenceis repeated with hands switched
    //                                      EXAMPLE: (6x,4)* = (6x,4)(4,6x)
    //                              object number is as usual
    //                                      EXAMPLE: (6x,4)* = (6+4)/2 =  5 objects
    //              -mulitiplex notation:
    //                              indicated by [ ]
    //                              object number is calculated by adding all digits inside brackets
    //                                      together, but only dividing by 1 per set of of brackets
    //                                         EXAMPLE: [3,3] = (3+3)/1 = 6 objects
    //              -transition throws should not be indicated in the siteswap, they should invalidate a siteswap
    //      -each line is treated as a different siteswap
    //      -only valid siteswaps are used

    //OPERATION SUPER EASY TO USE:-----------------------------------------------
    //  -Instead of just an intro dialog, there could be a sort of a walkthrough that shows the user where to click to go about
    //      and do different things. like some sort of dialog with arrows telling the user exactly where to go and click.
    //  -in whatever intro we end up using there should be a mention of things that the user should have a basic understanding of
    //      before using the app: siteswaps, what pattern variations are(mills mess, rubensteins revenge..)

    //INTRO/NOVICE APP:
    //  -it might be nice to have another simpler app for people who do not really know what they are doing or are new to juggling
    //  to more gradually understand the idea of what formic is about

    //NOTES FROM THE SHOPPING LIST:
    //  -the more it is left up to the app to keep track of what stuff is creating new records,
    //          the more the user needn't waste thought on it
    //  -if user is i habit of hitting start, but nt seeing immediate results, then just seeing
    //          general graphs over time, maybe it will be easier to correlate certain more long-term behavior
    //          w/ preferable results
    //  -If app sometimes tells you whether or not to record time after an endurance while actually in endurance, interesting
    //          combos of never knowing if currrent attempt is being recorded could occur
    //  -i like blurring the line b/w juggling & breathing to find commonalities. The more general an app can be made,
    //          the more general the feedback that can be analyzed is.
    //  -if an app could be made that could post general stats on everyone so that everyone can see the add, then everyone
    //          could help look for interesting trends.
    //  -we could use a timer that has a certain % chance of working so the user never knows if they are being recorded or not
    //  -a timer app w/ ai could give recommendations of what to do exactly when and then keep track of stuff on its own and even
    //          look for difference b/w times when user is making choices of what to do and when ai is making choices
    //                  -user could input what it is that they would like to be able to do. ex: hold breath for long time/
    //                                                                                          hold a pattern for long time..
    //  -timers that don't show time, so that the user just hits finished on the timer and doesn't know how they did.
    //      -it could even tell them that they broke a record, but not tell them every time they broke a record so they
    //              always have the chance that they did break a record
    //                 -WOW! this could go really great with the breath hold/O2 & CO2 tables since it could base your tables
    //                          of your personal best which it knows, but you don't and it could even tell you when to quit
    //                          holding your breath without you knowing how long you held it(in the tables)
    //  -voice recognition so that there is no need to even touch the phone
    //  -maybe use lightblue bean so that the bean could just be shook to indicate stuff
    //          -maybe the phones accelerometer could be used in this way.

    //NOTES ON SESSIONS/RUNS:---------------------------------------------
    //  -the date/time associated with runs is the date/time that the run ended, not the time that it began

    //Random thoughts from /r/juggling discussion
    //  -it is really vague, but maybe something to do with how sloppy you allow yourself to be, maybe just use a modifier which is a
    //      permitted sloppiness rating that you decide on before entering a run where 10 is anything goes and 1 is as rigid as you can juggle.
    //      Another way to do this would be to have modifiers for specific ways that you might be sloppy, such as 'feet can move', 'can reach
    //      forward'

    //SOME THOUGHTS TO ORGANIZE:
    //      -TO KNOW OR NOT TO KNOW(This is probably just a new kind of modifier, however I could see how it would be good
    //          as some sort of a contstant modifier). THERE COULD BE SOMETHING called constant modifiers(in settings) that are
    //          just always added to every run, but do not have to take up room in the main modifier input. It could be for things like
    //          standing, unaware of run time, barefoot, indoors... anything that the user does pretty much every session, but could possibly
    //          change
    //              -be able to check, unable to check the time during a run, there is a few different ways this could be:
    //                  nearly constantly aware of current time,
    //                  current time is completely unknown
    //                  able to check current time( I do not know if this is an important distinction from the first one or not)
    //  -whether or not app was indicating that a pb was broken with the audio should be kept track of with the run so that
    //      we can see if there is any coorelation between that audio and improvement
    //misc
    //      -programming easy ramp into programming for non-programers says something about programming





}



//NEW TODO:
//  POSSIBLE FIX FOR TIMER PROBLEM(timer doesnt always show the right time, i saw it call 6min 45sec)
//      -every time the number of seconds is going to be shown, check the current time against the
//              start time, and show that. this needs to also be done right before the time is added
//              to the DB as well(or rather, when the time that will be added to the DB is registered,
//              set it to the current time minus the start time)
//  -when the app starts it should have a bunch of siteswaps already loaded into it, as well as all the
//          modifiers that are described in the longhold on the modifiers. if a user
//          doesnt want these, they can always clear them and add their own.
//  once it is at all usable, work on adding it to the play store so that ppl can try it and discuss it