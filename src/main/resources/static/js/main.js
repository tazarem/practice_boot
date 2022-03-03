function fetchGet(url,param){
    return new Promise((resolve,reject)=>{
       $.ajax({
           type:'GET',
           url:url,
           data:param,
           success:(res)=>{
               resolve(res)
           },
           error:(err)=>{
               reject(err)
           }
       })
    })
   }
function fetchPost(url,param){
       return new Promise((resolve,reject)=>{
          $.ajax({
              type:'POST',
              url:url,
              data:param,
              success:(res)=>{
                  resolve(res)
              },
              error:(err)=>{
                  reject(err)
              }
          })
       })
}


async function logout(){
	//아약스로 세션 만료시키기
	let result = await fetchGet('/user/doLogout')
	if(result==1){
		alert('로그아웃 되었습니다')
        location.href="/"
	}
}



$(function(){
	
})