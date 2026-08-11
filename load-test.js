import http from 'k6/http';
import {check, sleep} from 'k6';

// [A] 부하 시나리오: VU(가상유저) 수를 시간에 따라 조절
export const options = {
    stages: [
        {duration: '30s', target: 100},  // 100명까지
        {duration: '30s', target: 300},  // 300명
        {duration: '30s', target: 500},  // 500명
        {duration: '20s', target: 0},
    ]
};

const BASE = 'http://localhost:8080';

// [B] setup(): 테스트 시작 전 딱 한번 -> 로그인해서 토큰 확보
export function setup() {
    const res = http.post(`${BASE}/api/v1/users/signin`, JSON.stringify({
        username: "test1",
        password: "1234",
    }), {headers: {'Content-Type': 'application/json'}});

    return {token: res.json('accessToken')}; // 토큰을 아래로 넘김
}

// [C] default(): 각 VU가 계속 반복하는 실제 부하 요청
export default function (data) {
    const res = http.get(`${BASE}/api/v1/posts/1`, {
        headers: {Authorization: `Bearer ${data.token}`}
    });

    check(res, {'status가 200인가': (r) => r.status === 200});
    // sleep(1); // 요청 사이 1초 쉼
}

