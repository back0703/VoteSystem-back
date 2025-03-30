package com.example.votesystem;

import com.example.votesystem.Vote;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/api/votes")
public class VoteController {
    private final Map<String, Vote> votes = new ConcurrentHashMap<>();

    // 创建投票
    @PostMapping("/create")
    public String createVote(
            @RequestParam String title,
            @RequestParam String[] options,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime) {
        String voteId = "vote_" + System.currentTimeMillis();
        Map<String, Integer> results = new ConcurrentHashMap<>();
        for (String option : options) {
            results.put(option, 0);
        }
        votes.put(voteId, new Vote(voteId, title, Arrays.asList(options), results, startTime, endTime));
        return voteId;
    }

    // 获取所有投票
    @GetMapping
    public Map<String, Vote> getAllVotes() {
        return votes;
    }

    // 参与投票
    @PostMapping("/vote")
    public String castVote(@RequestParam String voteId, @RequestParam String option) {
        Vote vote = votes.get(voteId);
        if (vote == null) {
            return "投票未找到！";
        }
        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(vote.getStartTime())) {
            return "投票尚未开始";
        }
        if (now.isAfter(vote.getEndTime())) {
            return "投票已结束";
        }
        vote.getResults().merge(option, 1, Integer::sum);
        return "投票成功！";
    }

    // 查看投票结果
    @GetMapping("/results")
    public Vote getResults(@RequestParam String voteId) {
        return votes.get(voteId);
    }
}
