package com.example.gaodejava.mapper;

import com.example.gaodejava.point;

import java.util.List;

public interface guojiajiMapper {
    List<point> selectAll();

    point selectByName(String name);

}
