// Geometry-based PICO adaptation of upstream effects.js shock diamonds and cityGround/citySky visual language.
import fs from 'node:fs';
import * as T from '../.scratch/upstream/flight-reference/vendor/three/three.module.js';
function material(color,unlit=false,opacity=1){const m=new T.MeshStandardMaterial({color,roughness:.72,metalness:.12,transparent:opacity<1,opacity});m.userData.unlit=unlit;return m;}
function mesh(root,g,m,x=0,y=0,z=0){const o=new T.Mesh(g,m);o.position.set(x,y,z);root.add(o);return o;}
function box(root,m,x,y,z,w,h,d){return mesh(root,new T.BoxGeometry(w,h,d),m,x,y,z)}
function save(root,path){root.updateMatrixWorld(true);const mats=[],groups=new Map();root.traverse(o=>{if(!o.isMesh)return;let g=o.geometry.index?o.geometry.toNonIndexed():o.geometry.clone();g.applyMatrix4(o.matrixWorld);let k=mats.indexOf(o.material);if(k<0){k=mats.length;mats.push(o.material)}if(!groups.has(k))groups.set(k,[]);groups.get(k).push(g)});
 const chunks=[],views=[],acc=[],meshes=[],nodes=[];let size=0;
 function attr(a,bounds=false){const b=Buffer.from(a.buffer,a.byteOffset,a.byteLength);const vi=views.length;views.push({buffer:0,byteOffset:size,byteLength:b.length,target:34962});chunks.push(b);size+=b.length;const obj={bufferView:vi,componentType:5126,count:a.length/3,type:'VEC3'};if(bounds){obj.min=[Infinity,Infinity,Infinity];obj.max=[-Infinity,-Infinity,-Infinity];for(let i=0;i<a.length;i++){obj.min[i%3]=Math.min(obj.min[i%3],a[i]);obj.max[i%3]=Math.max(obj.max[i%3],a[i])}}acc.push(obj);return acc.length-1;}
 for(const [k,gs]of groups){const count=gs.reduce((n,g)=>n+g.attributes.position.array.length,0),p=new Float32Array(count),n=new Float32Array(count),c=new Float32Array(count);let offset=0,hasColor=false;for(const g of gs){p.set(g.attributes.position.array,offset);n.set(g.attributes.normal.array,offset);if(g.attributes.color){c.set(g.attributes.color.array,offset);hasColor=true}else c.fill(1,offset,offset+g.attributes.position.array.length);offset+=g.attributes.position.array.length}const attributes={POSITION:attr(p,true),NORMAL:attr(n)};if(hasColor)attributes.COLOR_0=attr(c);meshes.push({primitives:[{attributes,material:k}]});nodes.push({mesh:meshes.length-1});}
 const bin=Buffer.concat(chunks),doc={asset:{version:'2.0',generator:'FocusFlight native scene assets'},extensionsUsed:['KHR_materials_unlit'],scene:0,scenes:[{nodes:nodes.map((_,i)=>i)}],nodes,meshes,materials:mats.map(m=>({pbrMetallicRoughness:{baseColorFactor:[...m.color.toArray(),m.opacity],metallicFactor:m.metalness,roughnessFactor:m.roughness},...(m.userData.unlit?{extensions:{KHR_materials_unlit:{}},emissiveFactor:m.color.toArray()}:{emissiveFactor:m.color.toArray().map(v=>v*.18)}),doubleSided:true,...(m.transparent?{alphaMode:'BLEND'}:{})})),buffers:[{byteLength:bin.length}],bufferViews:views,accessors:acc};let json=Buffer.from(JSON.stringify(doc));json=Buffer.concat([json,Buffer.alloc((4-json.length%4)%4,32)]);const h=Buffer.alloc(12),jh=Buffer.alloc(8),bh=Buffer.alloc(8);h.writeUInt32LE(0x46546c67);h.writeUInt32LE(2,4);h.writeUInt32LE(28+json.length+bin.length,8);jh.writeUInt32LE(json.length);jh.writeUInt32LE(0x4e4f534a,4);bh.writeUInt32LE(bin.length);bh.writeUInt32LE(0x004e4942,4);fs.writeFileSync(path,Buffer.concat([h,jh,json,bh,bin]));console.log(path,meshes.length,'draw groups',h.readUInt32LE(8),'bytes');}
const cyan=material('#39caff',true),white=material('#e8ffff',true),halo=material('#087bea',true,.22),amber=material('#ffc56b',true);
const flame=new T.Group();
// Upstream effects.js: 4.5 shock diamonds, narrowing downstream. Translated to a real mesh profile.
for(const [mat,width,length]of [[halo,.14,1],[material('#48cfff',true,.35),.075,.95],[white,.034,.78]]){
 const points=[];for(let i=0;i<=90;i++){let t=i/90;const d=Math.pow(.5+.5*Math.cos(t*4.5*Math.PI*2),3);points.push(new T.Vector2(Math.max(.001,width*(1-t)*(.4+.6*d)),-t*length))}
 mesh(flame,new T.LatheGeometry(points,16),mat);
}
save(flame,'app/src/main/assets/models/jet_plume.glb');
const spark=new T.Group();mesh(spark,new T.SphereGeometry(.018,6,4).scale(1,4,1),white);save(spark,'app/src/main/assets/models/jet_spark.glb');
const ring=new T.Group();mesh(ring,new T.TorusGeometry(.24,.008,6,48).rotateX(Math.PI/2),cyan);save(ring,'app/src/main/assets/models/lift_ring.glb');
const world=new T.Group();
const asphalt=material('#242c3d'),sidewalk=material('#526176'),paint=material('#d9dfdf',true),ground=material('#172739'),facades=['#34435b','#3d5265','#53627a','#25364d'].map(c=>material(c));
box(world,ground,0,-.12,-15,140,.1,140);box(world,asphalt,0,-.025,-18,4.8,.08,65);
for(const s of [-1,1]){box(world,sidewalk,s*3.1,.025,-18,1.4,.16,65);box(world,cyan,s*2.43,.11,-18,.025,.025,65)}
for(let z=8;z>-49;z-=1.6){box(world,paint,0,.03,z,.055,.012,.65);for(const x of [-1.2,1.2])box(world,paint,x,.03,z,.035,.012,.45)}
for(const z of [-10,-26]){box(world,asphalt,0,-.01,z,45,.08,4.2);for(let x=-2;x<=2;x+=.4)for(const dz of [-2.5,2.5])box(world,paint,x,.045,z+dz,.23,.014,.7)}
let seed=541;function rand(){seed=(1664525*seed+1013904223)>>>0;return seed/4294967296}
for(const s of [-1,1])for(let i=0;i<14;i++){
 const z=3-i*4.5,h=2+rand()*6,w=1.8+rand()*1.3,x=s*(4.5+rand()*.7),m=facades[i%4];box(world,m,x,h/2,z,w,h,2.9);box(world,facades[(i+1)%4],x,h+.12,z,w*.76,.24,2.6);
 for(let y=.55;y<h-.3;y+=.65)for(let k=-1;k<=1;k++)if(rand()>.22)box(world,rand()>.35?amber:cyan,x+k*w*.23,y,z+1.46,.20,.28,.016);
 for(let y=.55;y<h-.3;y+=.75)box(world,cyan,x-s*(w/2+.008),y,z,.016,.025,2.7);
 if(i%2===0){box(world,facades[0],s*2.8,1.1,z,.055,2.2,.055);box(world,facades[0],s*2.65,2.18,z,.35,.05,.12);box(world,amber,s*2.65,2.15,z,.32,.025,.1)}
}
// Low distant skyline keeps the horizon legible behind the street.
for(let i=0;i<35;i++){const x=(i-17)*3,h=3+rand()*10;box(world,facades[i%4],x,h/2,-68,2,h,4)}
for(const z of [-12,-23,-35]){const arch=mesh(world,new T.TorusGeometry(1.55,.035,6,64),cyan,0,2.1,z);}
const skyMat=material('#ffffff',true);const sky=new T.SphereGeometry(110,48,32);const colors=[];const top=new T.Color('#142b68'),mid=new T.Color('#88689b'),horizon=new T.Color('#ffc48d');
for(let i=0;i<sky.attributes.position.count;i++){const h=sky.attributes.position.getY(i)/110;const c=h>.22?mid.clone().lerp(top,Math.min(1,(h-.22)/.6)):horizon.clone().lerp(mid,Math.max(0,h/.22));colors.push(c.r,c.g,c.b)}sky.setAttribute('color',new T.Float32BufferAttribute(colors,3));mesh(world,sky,skyMat,0,0,0);
mesh(world,new T.SphereGeometry(4,32,20),material('#ffdfae',true),-16,14,-75);
const cloud=material('#d4bfd7',true,.65);for(let i=0;i<6;i++){const x=-55+rand()*110,y=17+rand()*17,z=-25-rand()*55;for(let j=0;j<3;j++)mesh(world,new T.SphereGeometry(1,12,8).scale(4+rand()*4,.5+rand(),1.5),cloud,x+j*3,y,z)}
save(world,'app/src/main/assets/models/skyline_street.glb');
