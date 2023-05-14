package com.example.gaodejava.mapper;

import com.example.gaodejava.line;
import com.example.gaodejava.point;

import java.util.List;

public interface lineMapper {
    List<line> selectAll();

    point selectByName(String name);

}
