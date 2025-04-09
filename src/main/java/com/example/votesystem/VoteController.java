package com.example.votesystem;

import jakarta.servlet.http.HttpServletRequest;
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
        // 校验时间逻辑
        if (endTime.isBefore(startTime)) {
            throw new IllegalArgumentException("结束时间不能早于开始时间");
        }
        if (endTime.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("结束时间不能早于当前时间");
        }
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
    public String castVote(@RequestParam String voteId,
                           @RequestParam String option,
                           // 注入HttpServletRequest
                           HttpServletRequest request) {
        Vote vote = votes.get(voteId);
        LocalDateTime now = LocalDateTime.now();
        if (vote == null) {
            return "投票未找到！";
        }
        if (now.isBefore(vote.getStartTime())) {
            return "投票尚未开始";
        }
        if (now.isAfter(vote.getEndTime())) {
            return "投票已结束";
        }
        // IP校验
        String ip = getClientIp(request); // 自定义方法获取真实IP
        if (vote.getVoterIPs().containsKey(ip)) {
            return "每个IP只能投票一次";
        }
        vote.getResults().merge(option, 1, Integer::sum);
        vote.getVoterIPs().put(ip, option); // 记录IP和选项
        return "投票成功！";
    }

    // 获取客户端真实IP（处理代理）
    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip.split(",")[0]; // 处理多级代理的情况
    }

    // 查看投票结果
    @GetMapping("/results")
    public Vote getResults(@RequestParam String voteId) {
        return votes.get(voteId);
    }
}
