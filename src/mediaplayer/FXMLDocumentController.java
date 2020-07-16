/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mediaplayer;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;
import static javafx.application.Platform.runLater;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.FileChooser;
import javafx.util.Duration;
import javafx.scene.input.MouseEvent;
import javafx.event.EventHandler;
import javafx.scene.layout.StackPane;


public class FXMLDocumentController implements Initializable {
    
    private MediaPlayer mediaPlayer;
   
     @FXML
    private MediaView mediaView;
     
    private String filepath;
    
    @FXML
    private Label time;
    
    @FXML
    private Slider slider;
    
    @FXML
    private Slider seekSlider;
    
    @FXML
    private Duration duration;
    
 
@FXML
protected void updateValues() {
if (time != null) {
runLater(() -> {
Duration currentTime = mediaPlayer.getCurrentTime();
time.setText(formatTime(currentTime, duration));

});
}
}

@FXML
private static String formatTime(Duration elapsed, Duration duration) {
   int intElapsed = (int)Math.floor(elapsed.toSeconds());
   int elapsedHours = intElapsed / (60 * 60);
   if (elapsedHours > 0) {
       intElapsed -= elapsedHours * 60 * 60;
   }
   int elapsedMinutes = intElapsed / 60;
   int elapsedSeconds = intElapsed - elapsedHours * 60 * 60 
                           - elapsedMinutes * 60;
 
   if (duration.greaterThan(Duration.ZERO)) {
      int intDuration = (int)Math.floor(duration.toSeconds());
      int durationHours = intDuration / (60 * 60);
      if (durationHours > 0) {
         intDuration -= durationHours * 60 * 60;
      }
      int durationMinutes = intDuration / 60;
      int durationSeconds = intDuration - durationHours * 60 * 60 - 
          durationMinutes * 60;
      if (durationHours > 0) {
         return String.format("%d:%02d:%02d/%d:%02d:%02d", 
            elapsedHours, elapsedMinutes, elapsedSeconds,
            durationHours, durationMinutes, durationSeconds);
      } else {
          return String.format("%02d:%02d/%02d:%02d",
            elapsedMinutes, elapsedSeconds,durationMinutes, 
                durationSeconds);
      }
      } else {
          if (elapsedHours > 0) {
             return String.format("%d:%02d:%02d", elapsedHours, 
                    elapsedMinutes, elapsedSeconds);
            } else {
                return String.format("%02d:%02d",elapsedMinutes, 
                    elapsedSeconds);
            }
        }
    }

     
    @FXML
    private void handleButtonAction(ActionEvent event) {
       FileChooser fileChooser=new FileChooser();
       FileChooser.ExtensionFilter filter=new FileChooser.ExtensionFilter("Select a file (*.mp4)","*.mp4");
       fileChooser.getExtensionFilters().add(filter);
       File file=fileChooser.showOpenDialog(null);
       filepath=file.toURI().toString();
      
       
       if(filepath != null)
       {
       Media media=new Media(filepath);
       mediaPlayer =new MediaPlayer(media);
       mediaView.setMediaPlayer(mediaPlayer);
      DoubleProperty width=mediaView.fitWidthProperty();
      DoubleProperty height=mediaView.fitHeightProperty();
       
      width.bind(Bindings.selectDouble(mediaView.sceneProperty(), "width"));
      height.bind(Bindings.selectDouble(mediaView.sceneProperty(), "height"));
       
       
       slider.setValue(mediaPlayer.getVolume() * 100);
       slider.valueProperty().addListener(new InvalidationListener() {
           @Override
           public void invalidated(Observable observable) {
             
              mediaPlayer.setVolume(slider.getValue()/100);
             
           }
       }
       );
       
       
       seekSlider.setStyle("-fx-background-color: linear-gradient(to right, #2D819D 0%, #484848 0%);");
       
     seekSlider.setOnMouseClicked(new EventHandler<MouseEvent>() {
           @Override
           public void handle(MouseEvent event) {
               
               mediaPlayer.seek(Duration.seconds(seekSlider.getValue()));
               
           }
       }
     );
     
     seekSlider.maxProperty().bind(Bindings.createDoubleBinding(
                            () -> mediaPlayer.getTotalDuration().toSeconds(),

                             mediaPlayer.totalDurationProperty()));
     

     
mediaPlayer.currentTimeProperty().addListener((Observable ov) -> {
updateValues();
});

mediaPlayer.setOnReady(() -> {
duration = mediaPlayer.getMedia().getDuration();
updateValues();
});


StackPane trackPane1 = (StackPane) slider.lookup(".track");

        slider.valueProperty().addListener(new ChangeListener<Number>() {
            public void changed(ObservableValue<? extends Number> ov, Number old_val, Number new_val) {
                String style = String.format("-fx-background-color: linear-gradient(to right, #2D819D %d%%, #969696 %d%%);",
                        new_val.intValue(), new_val.intValue());
                trackPane1.setStyle(style);
            }
        });

        trackPane1.setStyle("-fx-background-color: linear-gradient(to right, #2D819D 100%, #969696 0%);");
        
        
    mediaPlayer.currentTimeProperty().addListener(new ChangeListener<Duration>() {
        
           @Override
           public void changed(ObservableValue<? extends Duration> observable, Duration oldValue, Duration newValue) {
              
                if (newValue != null && seekSlider != null) {
                double percentage = (newValue.toSeconds()/mediaPlayer.getTotalDuration().toSeconds())*100;
                String cssValue = "-fx-background-color: linear-gradient(to right, #2D819D " +
                 percentage + "%, white " + percentage + "%)";
                StackPane sliderTrack = (StackPane) seekSlider.lookup(".track");
                sliderTrack.setStyle(cssValue);
                seekSlider.setValue(newValue.toSeconds());
           }}
       });
         
    
        
       mediaPlayer.play();
       
       }  
    }
    
    @FXML
    private void pauseVideo(ActionEvent event){
        mediaPlayer.pause();
    }
    
    @FXML
    private void playVideo(ActionEvent event){
        mediaPlayer.play();
        mediaPlayer.setRate(1);
    }
    
    @FXML
    private void stopVideo(ActionEvent event){
        mediaPlayer.stop();
    }
    
    @FXML
    private void fastVideo(ActionEvent event){
        mediaPlayer.setRate(1.5);
    }
    
    @FXML
    private void fasterVideo(ActionEvent event){
        mediaPlayer.setRate(2);
    }
    
    @FXML
    private void slowVideo(ActionEvent event){
        mediaPlayer.setRate(0.75);
    }
    
    @FXML
    private void slowerVideo(ActionEvent event){
        mediaPlayer.setRate(0.5);
    }
    
    @FXML
    private void exitVideo(ActionEvent event){
        System.exit(0);
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    

    private static class EventHandlerImpl implements EventHandler<MouseEvent> {

        public EventHandlerImpl() {
        }

        @Override
        public void handle(MouseEvent event) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
    }
    
}
