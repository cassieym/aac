package com.aac.device.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class Category extends GridCell {
    private List<Card> cards;
}
