package application;

import java.awt.Toolkit;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.prefs.Preferences;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.layout.HBox;
import javafx.util.Duration;

public class TimeKeeper {

	Toolkit toolkit;
	
	private int longBreakTimeDuration;
	private int shortBreakTimeDuration;
	private int workTimeDuration;
	
	private int timeCountDownTracker;
	private	int breakOrWorkTracker = -1;
	private int overAllTimeCountedInCurrentProject = 0;
	private int whichTimerPeriodIsPlaying = 0;
	private int amountOfShortBreaksLeftTillLongBreak = 3;
	private int amountOfShortBreaksLeftTillLongBreakTracker = 3;

	private String contiousMode;
	private String project = "All Pomorodo";
	private boolean playing = false;
	private boolean paused = false;
	
	Label lbTimer;
	Button btnPlayAndPause;
	HBox hboxTop;
	HBox hboxCenter;
	HBox hboxBottom;
	Spinner spinnerProjects;

	private Timeline longBreakTimeLine;
	private Timeline shortBreakTimeLine;
	private Timeline workTimeLIne;
	
	private Preferences pref;
	//Preference names
	private String longBreakDurationPrefString = "longBreakDuration";
	private String shortBreakDurationPrefString = "shortBreakDuration";
	private String workTimeDurationPrefString = "workTimeDuration";
	private String currentProjectPrefString = "currentProject";
	private String currentTimePrefString = "currentTime";
	private String totalTimeWorkingPrefString = "totalTimeWorking";

	private String resumeTimeBooleanPrefString = "resumeTimeBoolean";
    private String resumeTimePrefString = "resumeTime";
    private String resumeWhichTimerIsPlayingPrefString = "resumeWhichTimerIsPlaying";
    private String lengthTillLongBreakTrackerPrefString = "lengthTillLongBreakTracker";
    private String resumebreakOrWorkPrefString = "resumebreakOrWork";
	private String resumecurrentTimePrefString = "resumecurrentTime";
	private String resumePrefString = "resume";
	private String amountOfCyclesTillLongBreakPrefString = "amountOfCyclesTillLongBreak";
					
	//Common Keywords
	private String yesKeyWord = "Yes";
	private String spaceKeyWord = " ";
	private String totalKeyWord = " Total";

	private String play = "Play";
	private String pauseColor = "-fx-background-color: #FFFF00";
	
	/**
	 * This is teh constructor that lets you pass in values that we will be using
	 * @param lbTimer the label we will be editing
	 * @param contiousMode whether continouse mode is on or not
	 */
	TimeKeeper(Label lbTimer, String contiousMode, HBox hboxTop, HBox hboxCenter, HBox hboxBottom, Spinner spinnerProjects, Button btnPlayAndPause){
		this.btnPlayAndPause = btnPlayAndPause;
		this.spinnerProjects = spinnerProjects;
		this.hboxTop = hboxTop;
		this.hboxCenter = hboxCenter;
		this.hboxBottom = hboxBottom;
		this.contiousMode = contiousMode;
		this.lbTimer = lbTimer;
		toolkit = Toolkit.getDefaultToolkit();
		setValues();
	}
	
	/**
	 * This method will update the label
	 * @param seconds
	 */
	private void updateValues(int seconds){
		int minLeft = (seconds/60);
		int secondsLeft = (seconds - (minLeft * 60));
		if((seconds%60) == 0){
			lbTimer.setText(minLeft + ":" + "00");
		}else if(seconds == 0){
			lbTimer.setText("00:00");
		}else if(secondsLeft < 10){
			lbTimer.setText(minLeft + ":0" + secondsLeft);
		}else{
			lbTimer.setText(minLeft + ":" + secondsLeft);
		}
		pref.putInt(currentTimePrefString, overAllTimeCountedInCurrentProject);
	}
	
	/**
	 * This method sets the values incase the app is exited and come back
	 */
	private void setValues(){
		pref = Preferences.userRoot();
		longBreakTimeDuration = (pref.getInt(longBreakDurationPrefString, 10) * 60);
		shortBreakTimeDuration = (pref.getInt(shortBreakDurationPrefString, 5) * 60);
		workTimeDuration = (pref.getInt(workTimeDurationPrefString, 25) * 60);
		amountOfShortBreaksLeftTillLongBreak = pref.getInt(amountOfCyclesTillLongBreakPrefString, 3);
		timeCountDownTracker = workTimeDuration;
		amountOfShortBreaksLeftTillLongBreakTracker = amountOfShortBreaksLeftTillLongBreak;
	}
	
	/**
	 * This method will handle the button presses, and will run the appropriate thread for the timer
	 * @param btnPlayAndPause
	 */
	public void playAndPause(){
		if(playing == false){
			if(breakOrWorkTracker == 1){
				if(amountOfShortBreaksLeftTillLongBreakTracker > 0){
					
					if(paused == false){
						if(pref.getBoolean(resumePrefString, false) == false){
							timeCountDownTracker = shortBreakTimeDuration;
						}else{
							pref.putBoolean(resumePrefString, false);
						}
					}else{
						paused = false;
					}

					whichTimerPeriodIsPlaying = 1;
					playing = true;
					longBreakTimeLine = new Timeline();
					longBreakTimeLine.setCycleCount(Timeline.INDEFINITE);
					KeyFrame frame = new KeyFrame(Duration.seconds(1), new EventHandler<ActionEvent>() {
						
						@Override
						public void handle(ActionEvent event) {
							overAllTimeCountedInCurrentProject++;
							if(timeCountDownTracker > 0){
								updateValues(timeCountDownTracker);
								System.out.println(timeCountDownTracker);
								timeCountDownTracker--;
							}else{
								updateValues(timeCountDownTracker);
			            		playing = false;
			            		breakOrWorkTracker *= -1;
			            		amountOfShortBreaksLeftTillLongBreakTracker--;
								toolkit.beep();
								longBreakTimeLine.stop();
								if(contiousMode.equals(yesKeyWord)){
									playAndPause();
								}else{
									spinnerProjects.setDisable(false);
									hboxBottom.setStyle(pauseColor);
									hboxCenter.setStyle(pauseColor);
									hboxTop.setStyle(pauseColor);
									btnPlayAndPause.setText(play);
								}
							}
							
						}
					});
					longBreakTimeLine.getKeyFrames().add(frame);
					longBreakTimeLine.playFromStart();
				}else{
					
					if(paused == false){

						if(pref.getBoolean(resumePrefString, false) == false){
							timeCountDownTracker = longBreakTimeDuration;
						}else{
							pref.putBoolean(resumePrefString, false);
						}
					}else{
						paused = false;
					}

					amountOfShortBreaksLeftTillLongBreakTracker = amountOfShortBreaksLeftTillLongBreak;
					whichTimerPeriodIsPlaying = 2;
					playing = true;
					shortBreakTimeLine = new Timeline();
					shortBreakTimeLine.setCycleCount(Timeline.INDEFINITE);
					KeyFrame frame = new KeyFrame(Duration.seconds(1), new EventHandler<ActionEvent>() {
						
						@Override
						public void handle(ActionEvent event) {
							overAllTimeCountedInCurrentProject++;
			            	if(timeCountDownTracker > 0){
								updateValues(timeCountDownTracker);
			            		System.out.println(timeCountDownTracker);
			            		timeCountDownTracker--;
			            	}else{
								updateValues(timeCountDownTracker);
			            		playing = false;
			            		breakOrWorkTracker *= -1;
			            		toolkit.beep();
			            		shortBreakTimeLine.stop();
								if(contiousMode.equals(yesKeyWord)){
									playAndPause();
								}else{
									spinnerProjects.setDisable(false);
									hboxBottom.setStyle(pauseColor);
									hboxCenter.setStyle(pauseColor);
									hboxTop.setStyle(pauseColor);
									btnPlayAndPause.setText(play);
								}
			            	}
						}
					});
					shortBreakTimeLine.getKeyFrames().add(frame);
					shortBreakTimeLine.playFromStart();
				}
			}else{
				
				if(paused == false){

					if(pref.getBoolean(resumePrefString, false) == false){
						timeCountDownTracker = workTimeDuration;
					}else{
						pref.putBoolean(resumePrefString, false);
					}
				}else{
					paused = true;
				}
				whichTimerPeriodIsPlaying = 3;
				playing = true;

				workTimeLIne = new Timeline();
				workTimeLIne.setCycleCount(Timeline.INDEFINITE);
				KeyFrame frame = new KeyFrame(Duration.seconds(1), new EventHandler<ActionEvent>() {
					@Override
					public void handle(ActionEvent event) {
						overAllTimeCountedInCurrentProject++;
		            	if(timeCountDownTracker > 0){
							updateValues(timeCountDownTracker);
		            		System.out.println(timeCountDownTracker);
		            		timeCountDownTracker--;
		            	}else{
							updateValues(timeCountDownTracker);
		            		playing = false;
		            		breakOrWorkTracker *= -1;
		            		toolkit.beep();
		            		workTimeLIne.stop();
							if(contiousMode.equals(yesKeyWord)){
								playAndPause();
							}else{
								spinnerProjects.setDisable(false);
								hboxBottom.setStyle(pauseColor);
								hboxCenter.setStyle(pauseColor);
								hboxTop.setStyle(pauseColor);
								btnPlayAndPause.setText(play);
							}
		            	}
					}
				});
				
				workTimeLIne.getKeyFrames().add(frame);
				workTimeLIne.playFromStart();
			}
		}else{
			if(whichTimerPeriodIsPlaying == 1){
				longBreakTimeLine.pause();
				paused = true;
			}else if(whichTimerPeriodIsPlaying == 2){
				shortBreakTimeLine.pause();
				paused = true;
			}else{
				workTimeLIne.pause();
				paused = true;
			}
			playing = false;
		}
	}
	
	/**
	 * This method will ahndle the skip button to skip the timer to the next period
	 */
	public void skip(){
		if(playing == true || paused == true){
			if(whichTimerPeriodIsPlaying == 1){
				longBreakTimeLine.stop();
				amountOfShortBreaksLeftTillLongBreakTracker--;
			}else if(whichTimerPeriodIsPlaying == 2){
				shortBreakTimeLine.stop();
				amountOfShortBreaksLeftTillLongBreakTracker = amountOfShortBreaksLeftTillLongBreak;
			}else{
				workTimeLIne.stop();
			}
			playing = false;
			breakOrWorkTracker *= -1;
			paused = false;
			playAndPause();
		}else{
			System.out.println("Press Play First");
		}
	}
	
	/**
	 * this method resets the timer by canceling on and restarting it
	 */
	public void reset(){
		if(playing == true || paused == true){
			if(whichTimerPeriodIsPlaying == 1){
				overAllTimeCountedInCurrentProject = (overAllTimeCountedInCurrentProject - (longBreakTimeDuration - timeCountDownTracker));
				longBreakTimeLine.stop();
			}else if(whichTimerPeriodIsPlaying == 2){
				overAllTimeCountedInCurrentProject = (overAllTimeCountedInCurrentProject - (shortBreakTimeDuration - timeCountDownTracker));
				shortBreakTimeLine.stop();
			}else{
				overAllTimeCountedInCurrentProject = (overAllTimeCountedInCurrentProject - (workTimeDuration - timeCountDownTracker));
				workTimeLIne.stop();
			}
			playing = false;
			paused = false;
			playAndPause();
		}else{
			System.out.println("Press Play First");
		}
	}
	
	/**
	 * This method will only run if the projects are changed
	 * @param newVAlue
	 * @return
	 */
	public String hardReset(String newProjectName){
		Calendar cal = Calendar.getInstance();
		
		pref.put(currentProjectPrefString, newProjectName);
		
		btnPlayAndPause.setText(play);
		hboxBottom.setStyle(pauseColor);
		hboxCenter.setStyle(pauseColor);
		hboxTop.setStyle(pauseColor);

		pref.putInt(project, (pref.getInt(project, 0) + overAllTimeCountedInCurrentProject));
		pref.putInt((project + totalKeyWord), (pref.getInt(project, 0) + overAllTimeCountedInCurrentProject));
		pref.putInt((project + spaceKeyWord + cal.get(Calendar.DAY_OF_WEEK)), (pref.getInt(project, 0) + overAllTimeCountedInCurrentProject));
		
		pref.putInt(totalTimeWorkingPrefString, (pref.getInt(totalTimeWorkingPrefString, 0) + overAllTimeCountedInCurrentProject));

		this.project = newProjectName;
		
		overAllTimeCountedInCurrentProject = 0;
		updateValues(workTimeDuration);
		
		if(playing == true || paused == true){
			if(whichTimerPeriodIsPlaying == 1){
				longBreakTimeLine.stop();
			}else if(whichTimerPeriodIsPlaying == 2){
				shortBreakTimeLine.stop();
			}else{
				workTimeLIne.stop();
			}
			playing = false;
			paused = false;
		}
		return newProjectName;
	}
	
	/**
	 * This class wills  stop the timer
	 * and save its current position
	 */
	public void stop(){
		pref.putBoolean(resumeTimeBooleanPrefString, true);
		pref.putInt(resumeTimePrefString, timeCountDownTracker);
		pref.putInt(resumeWhichTimerIsPlayingPrefString, whichTimerPeriodIsPlaying);
		pref.putInt(lengthTillLongBreakTrackerPrefString, amountOfShortBreaksLeftTillLongBreakTracker);
		pref.putInt(resumebreakOrWorkPrefString, breakOrWorkTracker);
		pref.putInt(resumecurrentTimePrefString, overAllTimeCountedInCurrentProject);
		pref.putBoolean(resumePrefString, true);
		
		if(playing == true || paused == true){
			if(whichTimerPeriodIsPlaying == 1){
				longBreakTimeLine.stop();
			}else if(whichTimerPeriodIsPlaying == 2){
				shortBreakTimeLine.stop();
			}else{
				workTimeLIne.stop();
			}
			playing = false;
			paused = false;
		}else{
			System.out.println("Press Play First");
		}
	}
	
	/**
	 * this will keep the time
	 * this will keep which work or thing is
	 * and it will set all the values for them
	 */
	public void resume(){
		pref.putBoolean(resumeTimeBooleanPrefString, false);
		timeCountDownTracker = pref.getInt(resumeTimePrefString, timeCountDownTracker);
		whichTimerPeriodIsPlaying = pref.getInt(resumeWhichTimerIsPlayingPrefString, whichTimerPeriodIsPlaying);
		amountOfShortBreaksLeftTillLongBreakTracker = pref.getInt(lengthTillLongBreakTrackerPrefString, amountOfShortBreaksLeftTillLongBreakTracker);
		breakOrWorkTracker = pref.getInt(resumebreakOrWorkPrefString, breakOrWorkTracker);
		overAllTimeCountedInCurrentProject = pref.getInt(resumecurrentTimePrefString, overAllTimeCountedInCurrentProject);
	}
	
	/**
	 * This method runs only if resume is run when there is no time left to automatically start on the next 
	 * @param seconds
	 * @param workBreak
	 */
	public void resume(int seconds, int workBreak){
		pref.putBoolean(resumeTimeBooleanPrefString, false);
		timeCountDownTracker = seconds;
		whichTimerPeriodIsPlaying = workBreak;
		amountOfShortBreaksLeftTillLongBreakTracker = pref.getInt(lengthTillLongBreakTrackerPrefString, amountOfShortBreaksLeftTillLongBreakTracker);
		breakOrWorkTracker = pref.getInt(resumebreakOrWorkPrefString, breakOrWorkTracker);
		overAllTimeCountedInCurrentProject = pref.getInt(resumecurrentTimePrefString, overAllTimeCountedInCurrentProject);
	}
}
