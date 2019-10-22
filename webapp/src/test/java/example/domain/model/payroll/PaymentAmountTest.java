package example.domain.model.payroll;

import example.domain.model.timerecord.breaktime.NightBreakTime;
import example.domain.model.timerecord.evaluation.ActualWorkDateTime;
import example.domain.model.timerecord.timefact.EndDateTime;
import example.domain.model.timerecord.timefact.StartDateTime;
import example.domain.model.timerecord.timefact.WorkDate;
import example.domain.model.timerecord.timefact.WorkRange;
import example.domain.model.wage.HourlyWage;
import example.domain.model.wage.WageCondition;
import example.domain.model.timerecord.breaktime.DaytimeBreakTime;
import example.domain.type.date.Date;
import example.domain.type.time.InputTime;
import example.domain.type.time.Minute;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PaymentAmountTest {
    @DisplayName("作業時間と時給で賃金計算が行えること")
    @ParameterizedTest
    @CsvSource({
            // 通常
            "09:00, 10:00, 0, 0, 1000, 1000",
            // 深夜
            "23:00, 24:00, 0, 0, 1000, 1350",
            // 深夜（早朝）
            "01:00, 03:00, 0, 0, 1000, 2700",
            // 通常＋深夜＋休憩
            "20:00, 24:00, 30, 60, 1000, 2850",
            // 通常10時間（超過2時間）
            "09:00, 19:00, 0, 0, 1000, 10500",
            // 通常13時間＋深夜1時間（超過6時間）
            "8:00, 24:00, 60, 60, 1000, 15850",
            // 通常17時間＋深夜7時間（超過16時間）
            "0:00, 24:00, 0, 0, 1000, 30450"
    })
    void wage(String begin, String end, int breakMinute, int nightBreakMinute, int hourlyWage, int expected) {
        String[] splitBegin = begin.split(":");
        InputTime startTime = new InputTime(Integer.valueOf(splitBegin[0]), Integer.valueOf(splitBegin[1]));
        String[] splitEnd = end.split(":");
        InputTime endTime = new InputTime(Integer.valueOf(splitEnd[0]), Integer.valueOf(splitEnd[1]));

        WorkDate workDate = new WorkDate(new Date("2018-11-25"));
        ActualWorkDateTime actualWorkDateTime = new ActualWorkDateTime(
                new WorkRange(StartDateTime.from(workDate, startTime), EndDateTime.from(workDate, endTime)),
                new DaytimeBreakTime(new Minute(breakMinute)),
                new NightBreakTime(new Minute(nightBreakMinute)));
        WageCondition wageCondition = new WageCondition(new HourlyWage(hourlyWage));

        PaymentAmount paymentAmount = new PaymentAmount(actualWorkDateTime, wageCondition);

        assertEquals(expected, paymentAmount.value.value().intValue());
    }
}
