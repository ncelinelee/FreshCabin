const cacheVersion = 'freshcabin-mobile-v1';
const shell = ['./', './manifest.webmanifest', './icons/apple.png', './icons/icon-192.png', './icons/icon-512.png'];

self.addEventListener('install', event => {
  event.waitUntil(caches.open(cacheVersion).then(cache => cache.addAll(shell)));
});

self.addEventListener('activate', event => {
  event.waitUntil((async () => {
    const keys = await caches.keys();
    await Promise.all(keys.filter(key => key.startsWith('freshcabin-mobile-') && key !== cacheVersion)
      .map(key => caches.delete(key)));
    await self.clients.claim();
  })());
});

self.addEventListener('fetch', event => {
  const url = new URL(event.request.url);
  if (event.request.method !== 'GET' || url.origin !== self.location.origin) return;
  if (event.request.mode === 'navigate' && url.pathname === new URL('./', self.location).pathname) {
    event.respondWith(fetch(event.request).catch(() => caches.open(cacheVersion).then(cache => cache.match('./'))));
    return;
  }
  if (shell.slice(1).some(path => new URL(path, self.location).href === url.href)) {
    event.respondWith(caches.open(cacheVersion).then(async cache => (await cache.match(event.request)) || fetch(event.request)));
  }
});
