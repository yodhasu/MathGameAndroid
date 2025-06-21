package com.example.mathgame;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    TextView playerHP, enemyHP;
    ImageView playerPng, enemyPng;
    Button ans1, ans2, ans3;

    int playerHPValue = 100;
    int enemyHPValue = 1000;

    String correctExpression = "";
    int correctResult = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Link views
        playerHP = findViewById(R.id.playerHP);
        enemyHP = findViewById(R.id.enemyHP);
        playerPng = findViewById(R.id.playerPng);
        enemyPng = findViewById(R.id.enemyPng);
        ans1 = findViewById(R.id.ans1);
        ans2 = findViewById(R.id.ans2);
        ans3 = findViewById(R.id.ans3);

        updateHP();
        generateQuestions();

        // Button actions
        Button[] buttons = {ans1, ans2, ans3};
        for (Button btn : buttons) {
            btn.setOnClickListener(v -> handleAnswer(btn.getText().toString()));
        }
    }

    void updateHP() {
        playerHP.setText("HP: " + playerHPValue);
        enemyHP.setText("HP: " + enemyHPValue);
    }

    void handleAnswer(String input) {
        String[] parts = input.split("=");
        String expr = parts[0].trim();
        int selectedResult = Integer.parseInt(parts[1].trim());

        if (expr.equals(correctExpression)) {
            enemyHPValue -= correctResult;
            Toast.makeText(this, "Correct! Enemy took " + correctResult + " damage!", Toast.LENGTH_SHORT).show();
        } else {
            int damage = selectedResult;
            playerHPValue -= damage;
            Toast.makeText(this, "Wrong! You took " + damage + " damage!", Toast.LENGTH_SHORT).show();
        }

        // Clamp HP
        playerHPValue = Math.max(playerHPValue, 0);
        enemyHPValue = Math.max(enemyHPValue, 0);
        updateHP();

        if (enemyHPValue == 0) {
            Toast.makeText(this, "🎉 You Win!", Toast.LENGTH_LONG).show();
        } else if (playerHPValue == 0) {
            Toast.makeText(this, "💀 Game Over!", Toast.LENGTH_LONG).show();
        } else {
            generateQuestions();
        }
    }

    void generateQuestions() {
        Random rand = new Random();
        List<String> expressions = new ArrayList<>();

        correctExpression = buildExpression(rand);
        correctResult = (int) evaluate(correctExpression);
        expressions.add(correctExpression + " = " + correctResult);

        // Generate 2 fake ones
        while (expressions.size() < 3) {
            String fakeExpr = buildExpression(rand);
            int fakeResult = (int) evaluate(fakeExpr);

            if (!fakeExpr.equals(correctExpression) && fakeResult != correctResult) {
                expressions.add(fakeExpr + " = " + fakeResult);
            }
        }

        Collections.shuffle(expressions);
        ans1.setText(expressions.get(0));
        ans2.setText(expressions.get(1));
        ans3.setText(expressions.get(2));
    }

    String buildExpression(Random rand) {
        int[] nums = new int[4];
        String[] ops = new String[3];
        String[] symbols = {"+", "-", "*", "/"};

        for (int i = 0; i < 4; i++) {
            nums[i] = rand.nextInt(9) + 1;
        }

        for (int i = 0; i < 3; i++) {
            ops[i] = symbols[rand.nextInt(4)];

            if (ops[i].equals("/")) {
                // Make divisible to avoid decimals
                nums[i] = nums[i + 1] * (rand.nextInt(3) + 1);
            }
        }

        return nums[0] + ops[0] + nums[1] + ops[1] + nums[2] + ops[2] + nums[3];
    }

    double evaluate(String expression) {
        try {
            Expression expr = new ExpressionBuilder(expression).build();
            return expr.evaluate();
        } catch (Exception e) {
            return 0;
        }
    }
}
