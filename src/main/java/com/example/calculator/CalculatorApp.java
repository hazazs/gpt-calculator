package com.example.calculator;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class CalculatorApp extends JFrame implements ActionListener {

    private final JTextField display = new JTextField("0");
    private double firstOperand = 0;
    private String operator = null;
    private boolean startNewNumber = true;

    public CalculatorApp() {
        super("GPT Calculator");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(340, 460);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel root = new JPanel(new BorderLayout(10, 10));
        root.setBorder(new EmptyBorder(15, 15, 15, 15));
        setContentPane(root);

        display.setEditable(false);
        display.setHorizontalAlignment(SwingConstants.RIGHT);
        display.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 32));
        display.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.GRAY),
                new EmptyBorder(10, 10, 10, 10)
        ));
        root.add(display, BorderLayout.NORTH);

        JPanel buttons = new JPanel(new GridLayout(5, 4, 8, 8));
        String[] labels = {
                "C", "+/-", "%", "/",
                "7", "8", "9", "*",
                "4", "5", "6", "-",
                "1", "2", "3", "+",
                "0", ".", "DEL", "="
        };

        for (String label : labels) {
            JButton button = new JButton(label);
            button.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 18));
            button.setFocusPainted(false);
            button.addActionListener(this);
            buttons.add(button);
        }

        root.add(buttons, BorderLayout.CENTER);
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        String command = event.getActionCommand();

        if (command.matches("\\d")) {
            appendDigit(command);
        } else if (".".equals(command)) {
            appendDecimalPoint();
        } else if (command.matches("[+\\-*/]")) {
            chooseOperator(command);
        } else {
            switch (command) {
                case "=" -> calculateResult();
                case "C" -> clear();
                case "DEL" -> deleteLastCharacter();
                case "+/-" -> toggleSign();
                case "%" -> applyPercent();
                default -> { }
            }
        }
    }

    private void appendDigit(String digit) {
        if (startNewNumber || "0".equals(display.getText()) || "Error".equals(display.getText())) {
            display.setText(digit);
            startNewNumber = false;
        } else {
            display.setText(display.getText() + digit);
        }
    }

    private void appendDecimalPoint() {
        if (startNewNumber || "Error".equals(display.getText())) {
            display.setText("0.");
            startNewNumber = false;
        } else if (!display.getText().contains(".")) {
            display.setText(display.getText() + ".");
        }
    }

    private void chooseOperator(String nextOperator) {
        if (operator != null && !startNewNumber) {
            calculateResult();
        }

        firstOperand = parseDisplay();
        operator = nextOperator;
        startNewNumber = true;
    }

    private void calculateResult() {
        if (operator == null || "Error".equals(display.getText())) {
            return;
        }

        double secondOperand = parseDisplay();
        double result;

        switch (operator) {
            case "+" -> result = firstOperand + secondOperand;
            case "-" -> result = firstOperand - secondOperand;
            case "*" -> result = firstOperand * secondOperand;
            case "/" -> {
                if (secondOperand == 0) {
                    showError();
                    return;
                }
                result = firstOperand / secondOperand;
            }
            default -> throw new IllegalStateException("Unsupported operator: " + operator);
        }

        display.setText(formatNumber(result));
        firstOperand = result;
        operator = null;
        startNewNumber = true;
    }

    private void clear() {
        display.setText("0");
        firstOperand = 0;
        operator = null;
        startNewNumber = true;
    }

    private void deleteLastCharacter() {
        if (startNewNumber || "Error".equals(display.getText())) {
            return;
        }

        String value = display.getText();
        if (value.length() <= 1 || (value.length() == 2 && value.startsWith("-"))) {
            display.setText("0");
            startNewNumber = true;
        } else {
            display.setText(value.substring(0, value.length() - 1));
        }
    }

    private void toggleSign() {
        if ("Error".equals(display.getText()) || "0".equals(display.getText())) {
            return;
        }

        if (display.getText().startsWith("-")) {
            display.setText(display.getText().substring(1));
        } else {
            display.setText("-" + display.getText());
        }
    }

    private void applyPercent() {
        if ("Error".equals(display.getText())) {
            return;
        }
        display.setText(formatNumber(parseDisplay() / 100.0));
        startNewNumber = true;
    }

    private double parseDisplay() {
        return Double.parseDouble(display.getText());
    }

    private String formatNumber(double value) {
        if (value == Math.rint(value)) {
            return Long.toString((long) value);
        }
        return Double.toString(value);
    }

    private void showError() {
        display.setText("Error");
        firstOperand = 0;
        operator = null;
        startNewNumber = true;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new CalculatorApp().setVisible(true));
    }
}
