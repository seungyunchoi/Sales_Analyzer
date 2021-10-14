import java.awt.*;
import javax.swing.*;
public class MultiPanel extends JFrame {
   private JPanel mainPanel, subPanel1, subPanel2, subPanel3, subPanel4, subPanel5, subPanel6;
   public MultiPanel(JPanel _subpanel1, JPanel _subpanel2, JPanel _subpanel3, JPanel _subpanel4, JPanel _subpanel5,JPanel _subpanel6) {
      setTitle("Dashboard");
      mainPanel = new JPanel(); // main panel
      mainPanel.setLayout(new GridLayout(3, 6));
      mainPanel.add(new JLabel("Dashboard", SwingConstants.CENTER));
      mainPanel.setBackground(Color.blue);
      //mainPanel.setBorder(BorderFactory.createLineBorder(Color.black, 1));
      subPanel1 = _subpanel1; // sub-panel 1
      subPanel1.setBackground(Color.red);
      subPanel2 = _subpanel2; // sub-panel 2
      subPanel2.setBackground(Color.blue);
      subPanel3 = _subpanel3;
      subPanel4 = _subpanel4;
      subPanel5 = _subpanel5;
      subPanel6 = _subpanel6;
      mainPanel.add(subPanel1);
      mainPanel.add(subPanel2);
      mainPanel.add(subPanel3);
      mainPanel.add(subPanel4);
      mainPanel.add(subPanel5);
      //mainPanel.add(subPanel6);
      add(mainPanel);
      setSize(400, 300);
      setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      setLocationRelativeTo(null);
      setVisible(true);
   }
   public static void main(String[] args) {
   }
}