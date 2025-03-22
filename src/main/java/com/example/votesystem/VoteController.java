package com.example.votesystem;

import com.example.votesystem.Vote;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/api/votes")
public class VoteController {
    private final Map<String, Vote> votes = new ConcurrentHashMap<>();

    // 创建投票
    @PostMapping("/create")
    public String createVote(@RequestParam String title, @RequestParam String[] options) {
        String voteId = "vote_" + System.currentTimeMillis();
        Map<String, Integer> results = new ConcurrentHashMap<>();
        for (String option : options) {
            results.put(option, 0);
        }
        // 将 options 转换为 List<String>
        votes.put(voteId, new Vote(voteId, title, Arrays.asList(options), results));
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
        if (vote != null) {
            vote.getResults().merge(option, 1, Integer::sum);
            return "投票成功！";
        }
        return "投票未找到！";
    }

    // 查看投票结果
    @GetMapping("/results")
    public Vote getResults(@RequestParam String voteId) {
        return votes.get(voteId);
    }
}
