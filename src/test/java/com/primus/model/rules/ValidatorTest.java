package com.primus.model.rules;

import com.primus.model.deck.Card;
import com.primus.model.deck.Color;
import com.primus.model.deck.PrimusCard;
import com.primus.model.deck.Values;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ValidatorTest {
    private Validator validator;

    @BeforeEach
    void setUp() {
        validator = new ValidatorImpl();
    }

    @Test
    void testIsValidCardMatchingColor() {
        final Card topCard = new PrimusCard(Color.RED, Values.FOUR);
        final Card toValidate = new PrimusCard(Color.RED, Values.SEVEN);
        assertTrue(validator.isValidCard(topCard, toValidate), "Card with matching color should be valid.");
    }

    @Test
    void testIsValidCardMatchingValue() {
        final Card topCard = new PrimusCard(Color.RED, Values.FOUR);
        final Card toValidate = new PrimusCard(Color.BLUE, Values.FOUR);
        assertTrue(validator.isValidCard(topCard, toValidate), "Card with matching value should be valid.");
    }

    @Test
    void testIsValidCardBlackCard() {
        final Card topCard = new PrimusCard(Color.RED, Values.FOUR);
        final Card toValidate = new PrimusCard(Color.BLACK, Values.WILD);
        assertTrue(validator.isValidCard(topCard, toValidate), "Black card should always be valid.");
    }

    @Test
    void testIsValidCardInvalidCard() {
        final Card topCard = new PrimusCard(Color.RED, Values.FOUR);
        final Card toValidate = new PrimusCard(Color.BLUE, Values.SEVEN);
        assertFalse(validator.isValidCard(topCard, toValidate), "Card with no matching color or value should be invalid.");
    }

    @Test
    void testIsValidDefenseValidDrawTwo() {
        final Card topCard = new PrimusCard(Color.RED, Values.DRAW_TWO);
        final Card toValidate = new PrimusCard(Color.BLUE, Values.DRAW_TWO);
        assertTrue(validator.isValidDefense(topCard, toValidate), "DRAW_TWO should be a valid defense against another DRAW_TWO.");
    }

    @Test
    void testIsValidDefenseValidWildDrawFour() {
        final Card topCard = new PrimusCard(Color.BLACK, Values.WILD_DRAW_FOUR);
        final Card toValidate = new PrimusCard(Color.BLACK, Values.WILD_DRAW_FOUR);
        assertTrue(validator.isValidDefense(topCard, toValidate),
                "WILD_DRAW_FOUR should be a valid defense against another WILD_DRAW_FOUR.");
    }

    @Test
    void testIsValidDefenseInvalidDefense() {
        final Card topCard = new PrimusCard(Color.RED, Values.DRAW_TWO);
        final Card toValidate = new PrimusCard(Color.BLUE, Values.WILD_DRAW_FOUR);
        assertFalse(validator.isValidDefense(topCard, toValidate),
                "WILD_DRAW_FOUR should not be a valid defense against DRAW_TWO.");
    }

    @Test
    void testIsValidDefenseNonPenaltyCard() {
        final Card topCard = new PrimusCard(Color.RED, Values.FOUR);
        final Card toValidate = new PrimusCard(Color.BLUE, Values.DRAW_TWO);
        assertFalse(validator.isValidDefense(topCard, toValidate), "Non penalty card should not allow any defense.");
    }
}
