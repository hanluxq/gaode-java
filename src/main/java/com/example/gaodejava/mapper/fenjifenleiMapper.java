package com.example.gaodejava.mapper;

import com.example.gaodejava.point;

import java.util.List;

public interface fenjifenleiMapper {
    List<point> selectAll(String s);

    List<point> searchByKeyword(String[] keyword);

    point selectByName(String name);

}
