
import javax.imageio.ImageIO;
import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class MainGUI extends JFrame {
    private JPanel content;
    private JComboBox comboBox1;
    private JButton gunagButton;
    private JButton loopButton;
    private JLabel gungaImage;
    private JButton stopButton;
    private JLabel statusLabel;


    //    vars
    private List<Mixer.Info> mixers;

    private Clip clip;
    private String status = "idle";

    private ImageIcon gungaImg;

    public MainGUI() {
        super("Rajio");

        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setContentPane(content);
        this.setIconImage(gungaImg.getImage());
        this.pack();

        gunagButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try
                {
                    playClip();
                }
                catch (Throwable t)
                {
                    System.out.println(t.toString());
                }
            }
        });

        loopButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try
                {
                    loopClip();
                }
                catch (Throwable t)
                {
                    System.out.println(t.toString());
                }
            }
        });

        stopButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try
                {
                    stopClip();
                }
                catch (Throwable t)
                {
                    System.out.println(t.toString());
                }
            }
        });
    }

    private void createUIComponents() throws IOException {
        BufferedImage image = ImageIO.read(Objects.requireNonNull(MainGUI.class.getClassLoader().getResource("images/gunga.jpg")));
        gungaImg = new ImageIcon(image.getScaledInstance(120, 120,  java.awt.Image.SCALE_SMOOTH));

        gungaImage = new JLabel(gungaImg);

        Line.Info playbackLine = new Line.Info(SourceDataLine.class);
        mixers = filterDevices(playbackLine);

        comboBox1 = new JComboBox<>(mixers.toArray());

    }

    private void playClip() throws UnsupportedAudioFileException, IOException, InterruptedException {
        AudioInputStream inputStream = AudioSystem.getAudioInputStream(Objects.requireNonNull(MainGUI.class.getClassLoader().getResourceAsStream("sounds/gunag.wav")));
        Mixer.Info selectedMixer = mixers.get(comboBox1.getSelectedIndex());

        try
        {
            stopClip();

            clip = AudioSystem.getClip(selectedMixer);
            clip.open(inputStream);
            clip.start();
            clip.addLineListener(e -> {
                if (e.getType() == LineEvent.Type.STOP) {
                    setStatus("idle");
                }
            });

            setStatus("playing");
        }
        catch (Throwable t)
        {
            System.out.println(t.toString());
        }
    }

    private void loopClip() throws UnsupportedAudioFileException, IOException, InterruptedException {
        AudioInputStream inputStream = AudioSystem.getAudioInputStream(Objects.requireNonNull(MainGUI.class.getClassLoader().getResourceAsStream("sounds/gunag.wav")));
        Mixer.Info selectedMixer = mixers.get(comboBox1.getSelectedIndex());

        try
        {
            stopClip();

            clip = AudioSystem.getClip(selectedMixer);
            clip.open(inputStream);
            clip.loop(Clip.LOOP_CONTINUOUSLY);
            setStatus("looping");
        }
        catch (Throwable t)
        {
            System.out.println(t.toString());
        }
    }

    private void stopClip() {
        if (clip != null) clip.close();
        clip = null;
        setStatus("idle");
    }

    public void setStatus(String status) {
        this.status = status;
        statusLabel.setText(status);
    }

    private List<Mixer.Info> filterDevices(final Line.Info supportedLine) {
        return Arrays
                .stream(AudioSystem.getMixerInfo())
                .filter(info -> {
                    Mixer mixer = AudioSystem.getMixer(info);
                    return mixer.isLineSupported(supportedLine);
                })
                .collect(Collectors.toList());
    }
}
