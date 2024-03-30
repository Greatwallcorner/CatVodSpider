import javax.swing.*;
import java.awt.*;

public class VerticalFlowLayoutExample {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Vertical FlowLayout Example");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));

            // 添加多个按钮，它们将垂直排布
            for (int i = 1; i <= 5; i++) {
                JButton button = new JButton("Button " + i);
                panel.add(button);
            }

            frame.getContentPane().add(panel);

            frame.setSize(200, 300);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}
