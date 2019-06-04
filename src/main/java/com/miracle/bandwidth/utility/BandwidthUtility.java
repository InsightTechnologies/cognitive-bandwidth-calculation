package com.miracle.bandwidth.utility;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.stereotype.Service;

import com.miracle.bandwidth.controller.ResourceLeave;
import com.miracle.bandwidth.controller.SprintTest;
import com.miracle.database.bean.Release;
import com.miracle.database.bean.Resource;
import com.miracle.database.bean.Team;

@Service
public class BandwidthUtility {

	public List<SprintTest> retrieveSprintDetails(Release release, int duration) {
		List<SprintTest> releaseTest = new ArrayList<>();
		boolean isEndDate = false;
		for (int i = 0; i < release.getNoOfSprints(); i++) {

			LocalDateTime localDateTime = release.getStartDate().toInstant().atZone(ZoneId.systemDefault())
					.toLocalDateTime();
			localDateTime = localDateTime.plusDays(duration);
			Date sprintEndDate = Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());

			if (release.getEndDate().compareTo(sprintEndDate) < 0) {
				sprintEndDate = release.getEndDate();
				isEndDate = true;
			}
			SprintTest release1 = new SprintTest();
			release1.setStartDate(release.getStartDate());
			release1.setEndDate(sprintEndDate);
			int days = (int) ChronoUnit.DAYS.between(release.getStartDate().toInstant(), sprintEndDate.toInstant());
			release1.setDuration(days);
			release1.setName("Sprint:" + i);
			releaseTest.add(release1);

			if (isEndDate) {
				break;
			}
			release.setStartDate(sprintEndDate);
		}
		return releaseTest;
	}

	public Map<String, Double> calcVelocity(Team team, Map<String, Integer> resourceDataMap) {
		int velocity = team.getVelocity();
		int workingHours = team.getWorkingHours();
		double velocityRatio = workingHours / velocity;
		Map<String, Double> map = new HashMap<>();
		for (Entry<String, Integer> entrySet : resourceDataMap.entrySet()) {
			map.put(entrySet.getKey(), entrySet.getValue() / velocityRatio);
		}
		return map;
	}

	public Map<String, Integer> getResourceLeaves(String team, List<Resource> resourceList,
			List<SprintTest> sprintList) {
		Map<String, List<ResourceLeave>> resourceMap = new HashMap<>();
		for (Resource resource : resourceList) {

			if (resource.getTeamName().equalsIgnoreCase(team) && resource.getLeaveType() != null) {
				List<ResourceLeave> list = new ArrayList<>();
				ResourceLeave resourceLeave = new ResourceLeave();
				resourceLeave.setStartDate(resource.getStartDate());
				resourceLeave.setEndDate(resource.getEndDate());

				resourceLeave.setLeaveType(resource.getLeaveType());
				list.add(resourceLeave);

				if (resourceMap.containsKey(resource.getResourceName())) {
					List<ResourceLeave> list1 = resourceMap.get(resource.getResourceName());
					list1.addAll(list);
					resourceMap.put(resource.getResourceName(), list1);
				} else {
					resourceMap.put(resource.getResourceName(), list);
				}
			}
		}

		Map<String, Integer> map = new HashMap<>();
		for (SprintTest sprintData : sprintList) {
			int count = 0;
			Date ra = sprintData.getStartDate();
			for (int i = 0; i <= sprintData.getDuration(); i++) {
				LocalDateTime sprintStartLocal = ra.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
				Date sprintStartDate = Date.from(sprintStartLocal.atZone(ZoneId.systemDefault()).toInstant());

				for (Entry<String, List<ResourceLeave>> entrySet : resourceMap.entrySet()) {
					for (ResourceLeave resourceLeave : entrySet.getValue()) {

						int days = (int) ChronoUnit.DAYS.between(resourceLeave.getStartDate().toInstant(),
								resourceLeave.getEndDate().toInstant());
						Date da = resourceLeave.getStartDate();
						for (int a = 0; a <= days; a++) {
							LocalDateTime leaveLocal = da.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
							Date leaveStartDate = Date.from(leaveLocal.atZone(ZoneId.systemDefault()).toInstant());
							if (sprintStartDate.compareTo(leaveStartDate) == 0) {
								count++;
							}
							leaveLocal = leaveLocal.plusDays(1);
							da = Date.from(leaveLocal.atZone(ZoneId.systemDefault()).toInstant());

						}

					}
				}

				sprintStartLocal = sprintStartLocal.plusDays(1);
				ra = Date.from(sprintStartLocal.atZone(ZoneId.systemDefault()).toInstant());

			}
			map.put(sprintData.getName(), (sprintData.getDuration() + 1 - count) * 8);
		}
		System.out.println(map);
		return map;
	}
}
