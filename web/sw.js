const C='sl-v3';
self.addEventListener('install',e=>{self.skipWaiting();});
self.addEventListener('activate',e=>e.waitUntil(caches.keys().then(ks=>Promise.all(ks.filter(k=>k!==C).map(k=>caches.delete(k)))).then(()=>self.clients.claim())));
// Réseau d'abord (4 s max) : tu vois toujours la dernière version ; sans réseau, copie gardée en mémoire
self.addEventListener('fetch',e=>{
  if(e.request.method!=='GET')return;
  e.respondWith((async()=>{
    const c=await caches.open(C);
    try{
      const r=await Promise.race([fetch(e.request,{cache:'no-cache'}),new Promise((_,rej)=>setTimeout(()=>rej(new Error('t')),4000))]);
      if(r&&(r.ok||r.type==='opaque'))c.put(e.request,r.clone());
      return r;
    }catch(err){
      const hit=await c.match(e.request,{ignoreSearch:true});
      if(hit)return hit;
      throw err;
    }
  })());
});
