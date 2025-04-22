package festival.dev.domain.calendar.entity;

import festival.dev.domain.gorupTDL.entity.GroupCalendar;
import festival.dev.domain.user.entity.User;
import festival.dev.global.entity.BaseTime;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Getter
    public class Calendar extends BaseTime {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        private int every;

        private int part;

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "user_id")
        private User user;

        @ElementCollection(fetch = FetchType.LAZY)
        @CollectionTable(name = "calendar_TDL_id", joinColumns = @JoinColumn(name = "calendar_id"))
        private List<Calendar_tdl_ids> toDoListId;

        @ElementCollection(fetch = FetchType.LAZY)
        @CollectionTable(name = "group_calendar", joinColumns = @JoinColumn(name = "calendar_id"))
        private List<GroupCalendar> groupCalendarId;
    }