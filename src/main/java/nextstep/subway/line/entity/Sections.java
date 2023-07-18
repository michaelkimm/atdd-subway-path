package nextstep.subway.line.entity;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import nextstep.subway.station.entity.Station;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Sections {

    private final int FIRST_IDX = 0;

    @OneToMany(mappedBy = "line", orphanRemoval = true, cascade = CascadeType.PERSIST)
    private List<Section> sections = new ArrayList<>();

    private Sections(Section section) {
        this.sections.add(section);
    }

    public static Sections init(Section section) {
        return new Sections(section);
    }

    public List<Station> getStations() {
        List<Station> stations = new ArrayList<>();
        Station station = getFirstStation();
        Station lastStation = getLastStation();
        while (!station.equalsId(lastStation)) {
            stations.add(station);
            station = getDownStation(station);
        }
        stations.add(station);
        return stations;
    }

    private Station getDownStation(Station station) {
        return sections.stream()
                .filter(section -> section.getUpStation().equalsId(station))
                .findAny()
                .map(section -> section.getDownStation())
                .orElseThrow(() -> new IllegalArgumentException(String.format("하행역이 존재하지 않습니다. 기준역id:%s", station.getId())));
    }

    public void addSection(Section section) {
        Validator.validateEnrollment(this, section);

        if (newSectionUpStationMatchAnyUpStation(section)) {
            Section existingSection = getSectionByUpStation(section.getUpStation());
            existingSection.divideBy(section);
        } else if (newSectionDownStationMatchAnyDownStation(section)) {
            Section existingSection = getSectionByDownStation(section.getDownStation());
            existingSection.divideBy(section);
        }
        sections.add(section);
    }


    private Section getSectionByUpStation(Station upStation) {
        return sections.stream()
                .filter(s -> s.getUpStation().equalsId(upStation))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException(String.format("구간을 찾을 수 없습니다. 상행역id:%s", upStation.getId())));
    }

    private Section getSectionByDownStation(Station downStation) {
        return sections.stream()
                .filter(s -> s.getDownStation().equalsId(downStation))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException(String.format("구간을 찾을 수 없습니다. 하역id:%s", downStation.getId())));
    }

    public void remove(Station station) {
        Validator.validateDeletion(this, station);
        sections.remove(getLastSection());
    }

    public Section getLastSection() {
        return getSectionByDownStation(getLastStation());
    }

    public Station getFirstStation() {
        List<Station> downStations = getDownStations();
        return sections.stream()
                .filter(section -> !downStations.contains(section.getUpStation()))
                .map(section -> section.getUpStation())
                .findAny()
                .orElseThrow(() -> new RuntimeException("노선 내 상행종착역을 찾을 수 없습니다."));
    }

    public Station getLastStation() {
        List<Station> upStations = getUpStations();
        return sections.stream()
                .filter(section -> !upStations.contains(section.getDownStation()))
                .map(section -> section.getDownStation())
                .findAny()
                .orElseThrow(() -> new RuntimeException("노선 내 하행종착역을 찾을 수 없습니다."));
    }

    private int size() {
        return sections.size();
    }

    private boolean hasStation(Station downStation) {
        return getStations().contains(downStation);
    }

    private boolean equalsLastStation(Station station) {
        return getLastStation().equalsId(station);
    }

    private boolean newSectionUpStationMatchAnyUpStation(Section section) {
        return getUpStations().contains(section.getUpStation());
    }

    private boolean newSectionDownStationMatchAnyDownStation(Section section) {
        return getDownStations().contains(section.getDownStation());
    }

    private List<Station> getUpStations() {
        return sections.stream()
                .map(section -> section.getUpStation())
                .collect(Collectors.toList());
    }

    private List<Station> getDownStations() {
        return sections.stream()
                .map(section -> section.getDownStation())
                .collect(Collectors.toList());
    }

    private static class Validator {
        static void validateEnrollment(Sections sections, Section section) {

            if (newSectionUpStationMatchLastStation(sections, section)) {
                validateNewSectionUpStationEqualsLineDownStation(sections, section);
                validateNewSectionDownStationIsNewcomer(sections, section);
                return;
            }
            if (newSectionDownStationMatchTopStation(sections, section)) {
                return;
            }
            if (newSectionDownStationMatchLastStation(sections, section)) {
                return;
            }
            if (newStationUpStationMatchTopStation(sections, section)) {
                return;
            }

            // 새로운 역을 하행 종점역으로 등록할 경우엔 통과
            throw new IllegalArgumentException(String.format("새 구간을 등록할 수 없습니다. 새구간 상행역id:%s, 하행역id:%d",
                    section.getUpStation().getId(), section.getDownStation().getId()));
        }

        private static boolean newSectionDownStationMatchLastStation(Sections sections, Section section) {
            return sections.equalsLastStation(section.getDownStation());
        }

        private static boolean newSectionUpStationMatchLastStation(Sections sections, Section section) {
            return sections.equalsLastStation(section.getUpStation());
        }

        private static boolean newSectionDownStationMatchTopStation(Sections sections, Section section) {
            return sections.getFirstStation().equalsId(section.getDownStation());
        }
        private static boolean newStationUpStationMatchTopStation(Sections sections, Section section) {
            return sections.getUpStations().contains(section.getUpStation());
        }

        private static void validateDeletion(Sections sections, Station Station) {
            validateDeletionEqualsLineDownStation(sections, Station);
            validateTwoMoreSectionExists(sections);
        }

        private static void validateNewSectionDownStationIsNewcomer(Sections sections, Section section) {
            if (sections.hasStation(section.getDownStation())) {
                throw new IllegalArgumentException("새로운 구간의 하행역이 해당 노선에 등록되어있는 역임.");
            }
        }

        private static void validateNewSectionUpStationEqualsLineDownStation(Sections sections, Section section) {
            if (!section.getUpStation().equalsId(sections.getLastStation())) {
                throw new IllegalArgumentException("새로운 구간의 상행 역이 해당 노선에 등록되어있는 하행 종착역이 아님.");
            }
        }

        private static void validateTwoMoreSectionExists(Sections sections) {
            if (sections.size() == 1) {
                throw new IllegalArgumentException("상행 종점역과 하행 종점역만 존재합니다.");
            }
        }

        private static void validateDeletionEqualsLineDownStation(Sections sections, Station station) {
            if (!sections.getLastStation().equalsId(station)) {
                throw new IllegalArgumentException(String.format("노선의 마지막 역이 아닙니다. 역id:%s", station.getId()));
            }
        }
    }
}
