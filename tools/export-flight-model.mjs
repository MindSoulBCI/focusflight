// Local conversion of the user-supplied procedural vehicle geometry.
// Source provenance is recorded separately in THIRD_PARTY_NOTICES.md.
import fs from 'node:fs';
import * as THREE from '../.scratch/upstream/flight-reference/vendor/three/three.module.js';
import {loftGeos} from '../.scratch/upstream/flight-reference/js/render/loft.js';
const source=fs.readFileSync('.scratch/upstream/flight-reference/js/render/roadster.js','utf8');
const bodyCode=source.slice(source.indexOf('const BODY ='),source.indexOf('const SILL ='));
const glassCode=source.slice(source.indexOf('const GLASS ='),source.indexOf('\nconst box ='));
const {BODY,GLASS}=new Function('THREE',bodyCode+'\n'+glassCode+'\nreturn {BODY,GLASS}')(THREE);
const root=new THREE.Group();root.name='HoverCar';
const materials=[new THREE.MeshStandardMaterial({color:0xe3e7ea,metalness:.65,roughness:.3}),new THREE.MeshStandardMaterial({color:0x263544,metalness:.45,roughness:.18}),new THREE.MeshStandardMaterial({color:0x15181c,metalness:.6,roughness:.6}),new THREE.MeshStandardMaterial({color:0xe4a94a,emissive:0xe4a94a,emissiveIntensity:1})];
function add(geo,mat,x=0,y=0,z=0,rx=0,rz=0){const m=new THREE.Mesh(geo,materials[mat]);m.position.set(x,y,z);m.rotation.set(rx,0,rz);root.add(m);return m;}
for(const [k,g]of loftGeos(BODY,j=>j<2?2:0,2,30))add(g,k);
for(const [k,g]of loftGeos(GLASS,j=>j===1?0:1,undefined,40))add(g,k);
// Lift pods replace road wheels: no logos, plates, cabin screens or branded driver.
for(const x of [-1.45,1.45])for(const z of [-.98,.98]){
 add(new THREE.CylinderGeometry(.36,.36,.22,24),2,x,.27,z);
 add(new THREE.TorusGeometry(.29,.045,8,24),0,x,.13,z,Math.PI/2);
 add(new THREE.CylinderGeometry(.20,.24,.06,20),3,x,.10,z);
}
for(const z of [-.63,-.21,.21,.63]){add(new THREE.CylinderGeometry(.12,.15,.22,16),2,-2.23,.45,z,0,Math.PI/2);add(new THREE.CircleGeometry(.10,16),3,-2.35,.45,z,0,Math.PI/2);}
root.updateMatrixWorld(true);
const meshes=[],accessors=[],views=[],nodes=[],chunks=[];let length=0;
function attr(array,type,min,max){const b=Buffer.from(array.buffer,array.byteOffset,array.byteLength);const index=views.length;views.push({buffer:0,byteOffset:length,byteLength:b.length,target:34962});chunks.push(b);length+=b.length;while(length%4){chunks.push(Buffer.alloc(1));length++;}const a={bufferView:index,componentType:5126,count:array.length/3,type};if(min)a.min=min;if(max)a.max=max;accessors.push(a);return accessors.length-1;}
root.traverse(o=>{if(!o.isMesh)return;let g=o.geometry.index?o.geometry.toNonIndexed():o.geometry.clone();g.applyMatrix4(o.matrixWorld);g.computeBoundingBox();const b=g.boundingBox;const pi=attr(g.attributes.position.array,'VEC3',b.min.toArray(),b.max.toArray()),ni=attr(g.attributes.normal.array,'VEC3');meshes.push({name:'FlightPart'+meshes.length,primitives:[{attributes:{POSITION:pi,NORMAL:ni},material:materials.indexOf(o.material),mode:4}]});nodes.push({mesh:meshes.length-1});});
const bin=Buffer.concat(chunks);const doc={asset:{version:'2.0',generator:'FocusFlight asset conversion'},scene:0,scenes:[{nodes:[nodes.length]}],nodes:[...nodes,{name:'HoverCar',children:nodes.map((_,i)=>i)}],meshes,materials:materials.map(m=>({pbrMetallicRoughness:{baseColorFactor:[...m.color.toArray(),1],metallicFactor:m.metalness,roughnessFactor:m.roughness},emissiveFactor:m.emissive.toArray(),doubleSided:true})),buffers:[{byteLength:bin.length}],bufferViews:views,accessors};
let j=Buffer.from(JSON.stringify(doc));j=Buffer.concat([j,Buffer.alloc((4-j.length%4)%4,32)]);const hdr=Buffer.alloc(12);hdr.writeUInt32LE(0x46546c67);hdr.writeUInt32LE(2,4);hdr.writeUInt32LE(12+8+j.length+8+bin.length,8);const jh=Buffer.alloc(8);jh.writeUInt32LE(j.length);jh.writeUInt32LE(0x4e4f534a,4);const bh=Buffer.alloc(8);bh.writeUInt32LE(bin.length);bh.writeUInt32LE(0x004e4942,4);fs.writeFileSync('assets-source/hover_car.glb',Buffer.concat([hdr,jh,j,bh,bin]));
console.log({meshes:meshes.length,vertices:accessors.filter((_,i)=>i%2===0).reduce((n,a)=>n+a.count,0),bytes:hdr.readUInt32LE(8),bounds:new THREE.Box3().setFromObject(root)});
