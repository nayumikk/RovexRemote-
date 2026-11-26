package com.rovex.remote;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.content.Context;
import android.hardware.ConsumerIrManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.view.View;
import android.widget.*;

import java.util.ArrayList;

public class MainActivity extends Activity {
    private ConsumerIrManager ir;
    private TextView irStatus, tempText, powerText, signalText;
    private LinearLayout root;
    private int temp = 23;
    private boolean power = false;
    private String mode = "Охлаждение";
    private String fan = "Авто";

    private int color(String value) { return Color.parseColor(value); }

    @Override public void onCreate(Bundle state) {
        super.onCreate(state);
        ir = (ConsumerIrManager)getSystemService(Context.CONSUMER_IR_SERVICE);
        buildUi();
        refresh();
    }

    private GradientDrawable shape(String fill, int radius, String stroke) {
        GradientDrawable d = new GradientDrawable();
        d.setColor(color(fill)); d.setCornerRadius(radius);
        if (stroke != null) d.setStroke(1, color(stroke));
        return d;
    }

    private TextView label(String value, int size, String tint, boolean bold) {
        TextView t = new TextView(this);
        t.setText(value); t.setTextSize(size); t.setTextColor(color(tint));
        if (bold) t.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        return t;
    }

    private LinearLayout column() {
        LinearLayout v = new LinearLayout(this); v.setOrientation(LinearLayout.VERTICAL); return v;
    }
    private LinearLayout row() {
        LinearLayout v = new LinearLayout(this); v.setOrientation(LinearLayout.HORIZONTAL);
        v.setGravity(Gravity.CENTER_VERTICAL); return v;
    }
    private void gap(LinearLayout parent, int height) {
        View v = new View(this); parent.addView(v, new LinearLayout.LayoutParams(1, height));
    }
    private TextView button(String title, Runnable action, boolean selected) {
        TextView v = label(title, 14, selected ? "#071421" : "#EAF3FF", true);
        v.setGravity(Gravity.CENTER); v.setPadding(12, 15, 12, 15);
        v.setBackground(shape(selected ? "#72D7FF" : "#17243A", 22,
                selected ? "#72D7FF" : "#2B3B55"));
        v.setOnClickListener(view -> action.run());
        return v;
    }

    private void buildUi() {
        root = column(); root.setBackgroundColor(color("#0A1020")); root.setPadding(20, 22, 20, 24);
        ScrollView scroll = new ScrollView(this); scroll.setFillViewport(true); scroll.addView(root);
        setContentView(scroll);

        LinearLayout header = row();
        LinearLayout brand = column();
        brand.addView(label("ROVEX", 25, "#F4F8FF", true));
        brand.addView(label("UNITE  •  RS-07CST5", 12, "#91A4BE", false));
        header.addView(brand, new LinearLayout.LayoutParams(0, -2, 1));
        powerText = label("● ВЫКЛЮЧЕН", 12, "#91A4BE", true); header.addView(powerText);
        root.addView(header); gap(root, 20);

        LinearLayout hero = column(); hero.setGravity(Gravity.CENTER); hero.setPadding(20, 20, 20, 20);
        hero.setBackground(shape("#121E32", 30, "#243650"));
        hero.addView(label("ТЕСТОВЫЙ ПУЛЬТ", 11, "#91A4BE", true)); gap(hero, 5);
        tempText = label("23°", 60, "#F4F8FF", true); hero.addView(tempText);
        hero.addView(label("Тестовая установка температуры", 12, "#91A4BE", false)); gap(hero, 12);
        LinearLayout tempRow = row(); tempRow.setGravity(Gravity.CENTER);
        tempRow.addView(button("−", () -> { if (temp > 16) { temp--; refresh(); } }, false));
        TextView spacer = label("       ", 12, "#FFFFFF", false); tempRow.addView(spacer);
        tempRow.addView(button("+", () -> { if (temp < 30) { temp++; refresh(); } }, false));
        hero.addView(tempRow); root.addView(hero); gap(root, 14);

        root.addView(button("⏻   ВКЛ / ВЫКЛ (GREE TEST)", this::confirmSend, true));
        gap(root, 18);
        root.addView(label("РЕЖИМ", 12, "#91A4BE", true)); gap(root, 8);
        LinearLayout modes = row();
        for (String m : new String[]{"Авто", "Охлаждение", "Обогрев", "Осушение", "Вент."}) {
            TextView b = button(m, () -> { mode = m; refresh(); }, mode.equals(m));
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0, -2, 1);
            lp.setMargins(2, 0, 2, 0); modes.addView(b, lp);
        }
        root.addView(modes); gap(root, 16);

        root.addView(label("СКОРОСТЬ ВЕНТИЛЯТОРА", 12, "#91A4BE", true)); gap(root, 8);
        LinearLayout fans = row();
        for (String f : new String[]{"Авто", "1", "2", "3"}) {
            TextView b = button(f, () -> { fan = f; refresh(); }, fan.equals(f));
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0, -2, 1);
            lp.setMargins(2, 0, 2, 0); fans.addView(b, lp);
        }
        root.addView(fans); gap(root, 18);

        signalText = label("Пробный профиль: GREE, 38 кГц, 8 байт состояния. Совместимость с ROVEX не подтверждена.",
                12, "#F2C879", false);
        signalText.setPadding(14, 14, 14, 14);
        signalText.setBackground(shape("#241F18", 18, "#59472C")); root.addView(signalText);
        gap(root, 10);
        irStatus = label("", 12, "#91A4BE", false); root.addView(irStatus);
        gap(root, 8);
        root.addView(label("Важно: тест отправляет одну команду GREE ON / COOL / 23 °C. Если кондиционер её примет, он включится или переключится на охлаждение. Наведи верхнюю часть телефона на внутренний блок с расстояния 1–3 м.",
                11, "#91A4BE", false));
    }

    private void refresh() {
        tempText.setText(temp + "°");
        powerText.setText(power ? "● ТЕСТ: ON" : "● ГОТОВ К ТЕСТУ");
        powerText.setTextColor(color(power ? "#72D7FF" : "#91A4BE"));
        boolean available = ir != null && ir.hasIrEmitter();
        irStatus.setText(available ? "ИК-передатчик обнаружен Android." : "Android не обнаружил ИК-передатчик. На этой модели телефона передача может быть недоступна для сторонних приложений.");
        // Current UI keeps the initial test profile fixed so that each test is reproducible.
    }

    private void confirmSend() {
        if (ir == null || !ir.hasIrEmitter()) {
            new AlertDialog.Builder(this).setTitle("ИК-передатчик не найден")
                    .setMessage("Android не сообщает приложению доступный Consumer IR emitter. Сигнал не отправлен.")
                    .setPositiveButton("ОК", null).show();
            return;
        }
        new AlertDialog.Builder(this).setTitle("Отправить пробный GREE-сигнал?")
                .setMessage("Команда: включение, охлаждение, 23 °C, вентилятор Auto. Это экспериментальный протокол, совместимость с RS-07CST5 неизвестна. Если кондиционер примет сигнал, он может включиться или изменить режим.")
                .setNegativeButton("Отмена", null)
                .setPositiveButton("Отправить", (dialog, which) -> sendGreeTest())
                .show();
    }

    private void sendGreeTest() {
        try {
            int[] pattern = makeGreePattern();
            ir.transmit(38000, pattern);
            power = true;
            refresh();
            Toast.makeText(this, "Пробный GREE-сигнал отправлен", Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Toast.makeText(this, "Не удалось отправить ИК-сигнал: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    // GREE 8-byte state, test preset: YAW1F-style power-on, Cool, 23 C, Auto fan.
    // This is a protocol-family trial, NOT a confirmed ROVEX code.
    private int[] makeGreePattern() {
        int[] data = new int[]{0x09, 0x07, 0x60, 0x50, 0x00, 0x20, 0x00, 0xC0};
        ArrayList<Integer> p = new ArrayList<>();
        p.add(9000); p.add(4500);
        for (int i = 0; i < 4; i++) appendByte(p, data[i]);
        // GREE inter-block footer 010, MSB first, then a 19.98 ms gap.
        appendBit(p, 0); appendBit(p, 1); appendBit(p, 0);
        p.add(620); p.add(19980);
        for (int i = 4; i < 8; i++) appendByte(p, data[i]);
        p.add(620); p.add(19980);
        int[] result = new int[p.size()];
        for (int i = 0; i < p.size(); i++) result[i] = p.get(i);
        return result;
    }
    private void appendByte(ArrayList<Integer> p, int value) {
        for (int bit = 7; bit >= 0; bit--) appendBit(p, (value >> bit) & 1);
    }
    private void appendBit(ArrayList<Integer> p, int bit) {
        p.add(620); p.add(bit == 1 ? 1600 : 540);
    }
}
