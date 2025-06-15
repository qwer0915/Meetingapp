# 모바일서비스디자인 팀프로젝트 
프로젝트 이름 : 게시판 앱 제작
API를 통한 게시판 운영.
<br/>
2025-05-31 / 화면 구성
<br/>2025-06-08 / 로그인 , 회원가입 , 로그아웃 기능 정상 진행 완료
<br/>2025-06-14 / 메인페이지 , 게시글 등록 가능
<br/>2025-06-15 / 게시판 CRUD ,댓글 CR 가능
<br/>
<br/>
<br/>
- - -

현재 session에서 author 등록 안되는 문제 발생
api 서버 question create controller 코드 일부 수정
<br/>String username = (String) param.get("author");
			if (username == null) {
			    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("작성자 정보가 없습니다.");
			}
			param.put("author", username);
<br/>형식으로 변경했습니다.
