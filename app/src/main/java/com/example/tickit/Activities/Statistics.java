package com.example.tickit.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.os.Bundle;
import android.util.Log;

import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.HeatDataEntry;
import com.anychart.charts.HeatMap;
import com.anychart.enums.SelectionMode;
import com.anychart.graphics.vector.SolidFill;
import com.example.tickit.Callbacks.CallbackArrayListStrings;
import com.example.tickit.Callbacks.CallbackArrayListTasks;
import com.example.tickit.Callbacks.CallbackBoolean;
import com.example.tickit.Classes.AssumedTasksSituation;
import com.example.tickit.Classes.ProjectTask;
import com.example.tickit.DataBaseCalls.ProjectDatabaseCalls;
import com.example.tickit.DataBaseCalls.ProjectTasksDatabaseCalls;
import com.example.tickit.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Statistics extends AppCompatActivity {
    Map<String, Map<String,Integer>> data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        AnyChartView anyChartView = findViewById(R.id.chart);

        HeatMap projectSituation = AnyChart.heatMap();
        getData(new CallbackBoolean() {
            @Override
            public void callback(Boolean bool) {

            }
        });
        projectSituation.stroke("1 #fff");
        projectSituation.hovered()
                .stroke("2 #2fe6de")
                .fill(new SolidFill("#00171f", 1d))
                .labels("{ fontColor: '#2fe6de' }");

        projectSituation.interactivity().selectionMode(SelectionMode.NONE);

        projectSituation.title().enabled(true);
        projectSituation.title()
                .text("Situatia sarcinilor de lucru in cadrul proiectelor")
                .padding(0d, 0d, 20d, 0d);

        projectSituation.labels().enabled(true);
        projectSituation.labels()
                .minFontSize(14d)
                .format("function() {\n" +
                        "      var namesList = [\"0\", \"1\", \"2\", \"3\"];\n" +
                        "      return namesList[this.heat];\n" +
                        "    }");

        projectSituation.yAxis(0).stroke(null);
        projectSituation.yAxis(0).labels().padding(0d, 15d, 0d, 0d);
        projectSituation.yAxis(0).ticks(false);
        projectSituation.xAxis(0).stroke(null);
        projectSituation.xAxis(0).ticks(false);

        projectSituation.tooltip().title().useHtml(true);
        projectSituation.tooltip()
                .useHtml(true)
                .titleFormat("function() {\n" +
                        "      var namesList = [\"0\", \"1\", \"2\", \"3\"];\n" +
                        "      return '<b>' + namesList[this.heat] + '</b> Deadline-uri depasite';\n" +
                        "    }")
                .format("function () {\n" +
                        "       return '<span style=\"color: #CECECE\">Divizie: </span>' + this.x + '<br/>' +\n" +
                        "           '<span style=\"color: #CECECE\">Proiect: </span>' + this.y;\n" +
                        "   }");

        List<DataEntry> data = new ArrayList<>();

        data.add(new CustomHeatDataEntry("Rare", "Insigni", 0, "#90caf9"));
        data.add(new CustomHeatDataEntry("Rare", "Minor", 0, "#90caf9"));
        data.add(new CustomHeatDataEntry("Rare", "Moderate", 0, "#90caf9"));
        data.add(new CustomHeatDataEntry("Rare", "Major", 0, "#90caf9"));
        data.add(new CustomHeatDataEntry("Rare", "Extreme", 0, "#90caf9"));
        data.add(new CustomHeatDataEntry("Unlikely", "Insigni", 0, "#90caf9"));
        data.add(new CustomHeatDataEntry("Unlikely", "Minor", 0, "#90caf9"));
        data.add(new CustomHeatDataEntry("Unlikely", "Moderate", 0, "#90caf9"));
        data.add(new CustomHeatDataEntry("Unlikely", "Major", 1, "#ffb74d"));
        data.add(new CustomHeatDataEntry("Unlikely", "Extreme", 1, "#ffb74d"));
        data.add(new CustomHeatDataEntry("Possible", "Insigni", 0, "#90caf9"));
        data.add(new CustomHeatDataEntry("Possible", "Minor", 0, "#90caf9"));
        data.add(new CustomHeatDataEntry("Possible", "Moderate", 1, "#ffb74d"));
        data.add(new CustomHeatDataEntry("Possible", "Major", 1, "#ffb74d"));
        data.add(new CustomHeatDataEntry("Possible", "Extreme", 1, "#ffb74d"));
        data.add(new CustomHeatDataEntry("Likely", "Insigni", 0, "#90caf9"));
        data.add(new CustomHeatDataEntry("Likely", "Minor", 1, "#ffb74d"));
        data.add(new CustomHeatDataEntry("Likely", "Moderate", 1, "#ffb74d"));
        data.add(new CustomHeatDataEntry("Likely", "Major", 2, "#ef6c00"));
        data.add(new CustomHeatDataEntry("Likely", "Extreme", 2, "#ef6c00"));
        data.add(new CustomHeatDataEntry("Almost\\nCertain", "Insigni", 0, "#90caf9"));
        data.add(new CustomHeatDataEntry("Almost\\nCertain", "Minor", 1, "#ffb74d"));
        data.add(new CustomHeatDataEntry("Almost\\nCertain", "Moderate", 1, "#ffb74d"));
        data.add(new CustomHeatDataEntry("Almost\\nCertain", "Major", 2, "#ef6c00"));
        data.add(new CustomHeatDataEntry("Almost\\nCertain", "Extreme", 0, "#d84315"));

        projectSituation.data(data);


        anyChartView.setChart(projectSituation);
    }

    private void buildMaps(final CallbackBoolean callbackBoolean){
        ProjectDatabaseCalls.getProjectsNames(new CallbackArrayListStrings() {
            @Override
            public void onCallback(ArrayList<String> strings) {
                data = new HashMap<>();
                for(int i=1;i<getResources().getStringArray(R.array.departments).length;i++) {
                    Map<String, Integer> hashMap = new HashMap<>();
                    for (String project : strings) {
                        hashMap.put(project.replace(" ", ""), 0);
                        data.put(getResources().getStringArray(R.array.departments)[i], hashMap);
                    }
                }
                callbackBoolean.callback(true);
            }
        });
    }

    private void getData(CallbackBoolean callbackBoolean){
        buildMaps(new CallbackBoolean() {
            @Override
            public void callback(Boolean bool) {
                for(final String division:data.keySet()){
                    ProjectTasksDatabaseCalls.getTasksForDivision(division, "openTasks", new CallbackArrayListTasks() {
                        @Override
                        public void onCallBack(final ArrayList<ProjectTask> openTasks) {
                            ProjectTasksDatabaseCalls.getTasksForDivision(division, "assumedTasks", new CallbackArrayListTasks() {
                                @Override
                                public void onCallBack(ArrayList<ProjectTask> tasks) {

                                    tasks.addAll(openTasks);
                                    if(tasks.size()>0){
                                        for(ProjectTask task:tasks){
                                            if( task!=null && taskIsRisk(task) && data.get(division)!=null && data.get(division).get(task.getProject().getPath().substring(9))!=null){
                                                Map<String, Integer> projectValue = data.get(division);
                                                String key = task.getProject().getPath().substring(9);
                                                int newValue = projectValue.get(key)+1;
                                                projectValue.put(task.getProject().getPath().substring(9), newValue);
                                                data.put(division, projectValue);
                                            }
                                            Log.d("mandate", data.keySet().toArray()[data.size()-1].toString());
                                            if(tasks.indexOf(task)==tasks.size()-1 && data.keySet().toArray()[data.size()-1].toString().equals(division)){
                                                Log.d("mandate", data.keySet().toArray()[data.size()-1].toString());
                                            }

                                        }
                                    }

                                }
                            });
                        }
                    });
                }
            }
        });
    }


    private boolean taskIsRisk(ProjectTask task) {
        return Calendar.getInstance().getTime().after(task.getStopDate()) && !hasEveryoneFinished(task) && MainActivity.getContext()!=null;
    }

    private boolean hasEveryoneFinished(ProjectTask task) {
        if(task.getMembersWhoAssumed()  ==null|| task.getMembersWhoAssumed().size()<1){
            return false;
        }
        if(task.getMembersWhoAssumed().size()<task.getNumberOfVolunteers()){
            return false;
        }
        for (AssumedTasksSituation situation : task.getMembersWhoAssumed()){
            if(situation.getProgress()<2){
                return false;
            }
        }
        return true;
    }

    private class CustomHeatDataEntry extends HeatDataEntry {
        CustomHeatDataEntry(String x, String y, Integer heat, String fill) {
            super(x, y, heat);
            setValue("fill", fill);
        }
    }
}


