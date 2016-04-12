package com.jdistance.hadoop;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.IOException;

public class GraphsMapper extends Mapper<IntWritable, Text, IntWritable, Text> {
    @Override
    protected void map(IntWritable key, Text value, Context context) throws IOException, InterruptedException {
        super.map(key, value, context);
    }
}
